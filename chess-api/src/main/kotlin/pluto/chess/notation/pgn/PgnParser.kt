package pluto.chess.notation.pgn

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.GameResult
import pluto.chess.board.MoveConsumerResult
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.move.Conversion
import pluto.chess.move.MoveWithBoard
import pluto.chess.notation.parseFen
import java.io.InputStream
import java.lang.Integer.min
import java.util.regex.Matcher
import java.util.regex.Pattern

class PgnParser private constructor(private val input: String, private val handler: PgnParserHandler) {

    private val newlineMatcher = NEWLINE.matcher(input)
    private val tagMatcher = TAG.matcher(input)
    private val moveNumberMatcher = MOVE_NUMBER.matcher(input)
    private val moveMatcher = MOVE.matcher(input)
    private val commentMatcher = COMMENT.matcher(input)
    private val numericAnnotationMatcher = NUMERIC_ANNOTATION.matcher(input)
    private val beginVariationMatcher = BEGIN_VARIATION.matcher(input)
    private val endVariationMatcher = END_VARIATION.matcher(input)
    private val resultMatcher = RESULT.matcher(input)
    private var position = 0
    private var line = 1
    private var board: Board? = null
    private var result = ""

    private fun parse() {
        while (position < input.length) {
            val result = parseTags()
            if (result == PgnParserHandlerResult.CANCEL) {
                break
            }
            val active = parseMoves(result == PgnParserHandlerResult.CONTINUE)
            parseEnd(active)
        }
    }

    private fun parseResult(result: String) = when (result) {
        "1-0" -> GameResult.WHITE_WON
        "0-1" -> GameResult.BLACK_WON
        "1/2-1/2" -> GameResult.REMIS
        "*" -> GameResult.UNDEFINED
        else -> syntaxError()
    }

    private fun parseTags(): PgnParserHandlerResult {
        val builder = PgnMetaDataBuilder()
        var board: Board? = null
        while (matches(tagMatcher)) {
            val name = tagMatcher.group("name") ?: syntaxError()
            val value = tagMatcher.group("value") ?: syntaxError()
            when (name) {
                "Event" -> builder.event(value)
                "Site" -> builder.site(value)
                "White" -> builder.white(value)
                "Black" -> builder.black(value)
                "Date" -> builder.date(value)
                "Round" -> builder.round(value)
                "Result" -> {
                    result = value; builder.result(parseResult(value))
                }
                "SetUp" -> {}
                "FEN" -> board = Board.parseFen(value)
                else -> builder.data(name, value)
            }
        }
        if (board == null) {
            board = Board.newGame()
        }
        val ret = handler.setMetaData(builder.build())
        if (ret == PgnParserHandlerResult.CONTINUE) {
            this.board = board
            handler.setInitialBoard(board)
        }
        return ret
    }

    private fun parseMoves(active: Boolean): Boolean {
        var index = 0
        var ret = active
        while (true) {
            if (matches(moveNumberMatcher)) {
                if (ret) {
                    if ((moveNumberMatcher.group("dots") == "." && board?.colorOnTurn != FigureColor.WHITE) ||
                        (moveNumberMatcher.group("dots") == "..." && board?.colorOnTurn != FigureColor.BLACK)
                    ) {
                        semanticError("Wrong color on turn")
                    }
                    index = moveNumberMatcher.group("number").toInt()
                    index = if (board?.colorOnTurn == FigureColor.WHITE) index * 2 - 2 else (index * 2 - 1)
                }
            } else if (matches(moveMatcher)) {
                if (ret) {
                    ret = processMove(moveMatcher, index++)
                }
            } else if (matches(commentMatcher)) {
                // val comment = commentMatcher.group("comment")
            } else if (matches(numericAnnotationMatcher)) {
                // unprocessed
            } else if (matches(beginVariationMatcher)) {
                parseVariations()
            } else {
                break
            }
        }
        return ret
    }

    private fun parseVariations() {
        while (true) {
            if (matches(endVariationMatcher)) {
                break
            } else if (matches(beginVariationMatcher)) {
                parseVariations()
            } else if (!(matches(moveNumberMatcher) || matches(moveMatcher) || matches(commentMatcher) || matches(
                    numericAnnotationMatcher
                ))
            ) {
                syntaxError()
            }
        }
    }

    private fun processMove(moveMatcher: Matcher, index: Int): Boolean {
        val moveWithBoard: MoveWithBoard = convertToMove(moveMatcher)
        val previousBoard = this.board ?: throw ChessException("Board is not initialized")
        this.board = moveWithBoard.board
        return handler.addMove(moveWithBoard.move, index, previousBoard, moveWithBoard.board)
    }

    private fun convertToMove(moveMatcher: Matcher): MoveWithBoard {
        val castle = moveMatcher.group("castle")
        if (castle != null) {
            val startField = if (board?.colorOnTurn == FigureColor.WHITE) Field.E._1 else Field.E._8
            val targetField = if (castle.length == 3) startField shiftLine 2 else startField.shiftLine(-2)
            return board?.findMove(FigureType.KING, startField, targetField)
                ?: semanticError("Move ${moveMatcher.group()} not possible")
        } else {
            val target = moveMatcher.group("end") ?: semanticError("Move ${moveMatcher.group()} not possible")
            val type = moveMatcher.group("figure")?.let { parseFigure(it) } ?: FigureType.PAWN
            val targetField = parseField(target)
            val startLine = moveMatcher.group("startLine")?.let { parseLine(it[0]) }
            val startRow = moveMatcher.group("startRow")?.let { parseRow(it[0]) }
            val conversionFigure = moveMatcher.group("conversion")?.let { parseFigure(it) }
            val conversion = if (conversionFigure != null) Conversion.forFigure(conversionFigure) else null
            val moves = mutableListOf<MoveWithBoard>()
            board?.findMovesTo(type, targetField, conversion) {
                if ((startLine == null || it.move.startField.line == startLine)
                    && (startRow == null || it.move.startField.row == startRow)
                ) {
                    moves.add(it)
                }
                MoveConsumerResult.CONTINUE_ALL
            }
            if (moves.size == 0) {
                semanticError("Move ${moveMatcher.group()} not possible")
            } else if (moves.size > 1) {
                semanticError("Ambiguous move ${moveMatcher.group()}")
            } else {
                return moves[0]
            }
        }
    }

    private fun parseLine(line: Char): Int = line - 'a'

    private fun parseRow(row: Char): Int = row - '1'

    private fun parseField(field: String): Field {
        val row = parseRow(field[1])
        val line = parseLine(field[0])
        return Field(line + row * 8)
    }

    private fun parseFigure(type: String): FigureType = when (type) {
        "P" -> FigureType.PAWN
        "N" -> FigureType.KNIGHT
        "B" -> FigureType.BISHOP
        "R" -> FigureType.ROOK
        "Q" -> FigureType.QUEEN
        "K" -> FigureType.KING
        else -> semanticError("Illegal figure $type")
    }

    private fun parseEnd(active: Boolean) {
        if (this.matches(resultMatcher)) {
            val score = resultMatcher.group("score")
            if (score != result) {
                semanticError("Unexpected result $score")
            }
            val result = parseResult(score)
            if (active) {
                board?.let { handler.end(it, result) }
            }
        } else {
            syntaxError()
        }
    }

    private fun matches(matcher: Matcher): Boolean {
        val ret = matcher.find(position) && matcher.start() == position
        if (ret) {
            processNewLines(matcher)
        }
        return ret
    }

    private fun processNewLines(matcher: Matcher) {
        newlineMatcher.region(position, matcher.end())
        position = matcher.end()
        handler.setProgress(position, input.length)
        while (newlineMatcher.find()) {
            ++line
        }
    }

    private fun syntaxError(): Nothing = throw ChessException(
        "Syntax error in line $line '${
            input.substring(position, min(position + 10, input.length))
        }'"
    )

    private fun semanticError(message: String): Nothing = throw ChessException("Error in line $line: $message")

    companion object {
        private val NEWLINE = Pattern.compile("\\r\\n|\\n\\r|\\r|\\n")
        private val TAG = Pattern.compile("\\s*\\[\\s*(?<name>\\p{Alnum}+)\\s*\\\"(?<value>[^\\\"]*)\\\"\\s*\\]\\s*")
        private val MOVE_NUMBER = Pattern.compile("\\s*(?<number>\\d+)\\s*(?<dots>\\.(?:\\.\\.)?)\\s*")
        private val MOVE =
            Pattern.compile("(?:(?<figure>[PNBRQK])?(?<startLine>[a-h])?(?<startRow>[1-8])?x?(?<end>[a-h][1-8])(?:=(?<conversion>[NBRQ]))?|(?<castle>O-O-O|O-O|0-0-0|0-0))(?<check>[+#])?\\s*")
        private val COMMENT = Pattern.compile("\\s*\\{(?<comment>[^\\}]*)\\}\\s*")
        private val NUMERIC_ANNOTATION = Pattern.compile("\\s*\\\$\\d+\\s*")
        private val BEGIN_VARIATION = Pattern.compile("\\s*\\(\\s*")
        private val END_VARIATION = Pattern.compile("\\s*\\)\\s*")
        private val RESULT = Pattern.compile("\\s*(?<score>1-0|0-1|1/2-1/2|\\*)\\s*")

        fun parse(input: String, handler: PgnParserHandler) = PgnParser(input, handler).parse()

        fun parse(inputStream: InputStream, handler: PgnParserHandler) =
            parse(inputStream.bufferedReader().readText(), handler)
    }
}