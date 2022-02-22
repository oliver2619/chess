package pluto.chess.notation

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.BoardEditor
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.figure.Figure
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.figure.Piece
import java.util.regex.Pattern

object Fen : BoardNotation {

    private val FEN_PATTERN =
        Pattern.compile("^\\s*(?<figures>[pPnNbBrRqQkK0-8/]+)\\s+(?<color>[wWbB])\\s+(?<castle>[kKqQ]+|\\-)\\s+(?<ep>[a-h][1-8]|\\-)\\s+(?<fiftyMoves>\\d+)\\s+(?<moves>\\d+)\\s*")

    override fun boardToString(board: Board, totalMoves: Int): String {
        val ret = StringBuilder()
        figuresToString(board, ret)
        colorOnTurnToString(board.colorOnTurn, ret)
        castleFlagsToString(board.flags, ret)
        enPassantToString(board, ret)
        movesToString(board.fiftyMoves, totalMoves, ret)
        return ret.toString()
    }

    override fun parseBoard(input: String): Board {
        val matcher = FEN_PATTERN.matcher(input)
        if (matcher.find()) {
            val editor = parseFigures(matcher.group("figures"))
            val colorOnTurn = if (matcher.group("color").lowercase() == "w") FigureColor.WHITE else FigureColor.BLACK
            val flags = parseFlags(matcher.group("castle"), matcher.group("ep"))
            return editor.resume(colorOnTurn, flags, Integer.parseInt(matcher.group("fiftyMoves")))
        } else {
            throw ChessException("Failed to parse FEN string")
        }
        TODO("Not yet implemented")
    }

    private fun movesToString(fiftyMoves: Int, totalMoves: Int, ret: StringBuilder) {
        ret.append(fiftyMoves).append(' ').append(totalMoves / 2 + 1)
    }

    private fun colorOnTurnToString(color: FigureColor, ret: StringBuilder) {
        ret.append(if (color == FigureColor.WHITE) "w" else "b").append(' ')
    }

    private fun figuresToString(board: Board, ret: StringBuilder) {
        for (row in 7 downTo 0) {
            var spaces = 0
            for (line in 0..7) {
                val figure = board.getFigureOnField(Field(line + row * 8))
                if (figure == null) {
                    ++spaces
                } else {
                    if (spaces > 0) {
                        ret.append(spaces.toString())
                        spaces = 0
                    }
                    ret.append(figureToString(figure))
                }
            }
            if (spaces > 0) {
                ret.append(spaces.toString())
            }
            if (row > 0) {
                ret.append('/')
            }
        }
        ret.append(' ')
    }

    private fun figureToString(figure: Figure): String {
        val ret = when (figure.type) {
            FigureType.PAWN -> 'P'
            FigureType.KNIGHT -> 'N'
            FigureType.BISHOP -> 'B'
            FigureType.ROOK -> 'R'
            FigureType.QUEEN -> 'Q'
            FigureType.KING -> 'K'
        }
        return if (figure.color == FigureColor.WHITE) ret.uppercase() else ret.lowercase()
    }

    private fun castleFlagsToString(flags: Int, ret: StringBuilder) {
        if ((flags and Flags.CASTLE_ALL) == 0) {
            ret.append('-')
        } else {
            if ((flags and Flags.CASTLE_WHITE_KING_SIDE) == Flags.CASTLE_WHITE_KING_SIDE) {
                ret.append('K')
            }
            if ((flags and Flags.CASTLE_WHITE_QUEEN_SIDE) == Flags.CASTLE_WHITE_QUEEN_SIDE) {
                ret.append('Q')
            }
            if ((flags and Flags.CASTLE_BLACK_KING_SIDE) == Flags.CASTLE_BLACK_KING_SIDE) {
                ret.append('k')
            }
            if ((flags and Flags.CASTLE_BLACK_QUEEN_SIDE) == Flags.CASTLE_BLACK_QUEEN_SIDE) {
                ret.append('q')
            }
        }
        ret.append(' ')
    }

    private fun enPassantToString(board: Board, ret: StringBuilder) {
        if ((board.flags and Flags.EN_PASSANT_ENABLED) == Flags.EN_PASSANT_ENABLED) {
            val line = board.flags and Flags.EN_PASSANT_LINE_MASK
            ret.append('a' + line)
            ret.append(if (board.colorOnTurn == FigureColor.WHITE) '6' else '3')
        } else {
            ret.append('-')
        }
        ret.append(' ')
    }

    private fun parseFigures(figures: String): BoardEditor {
        val rows = figures.split('/')
        if (rows.size != 8) {
            throw ChessException("Failed to parse FEN string")
        }
        val editor = BoardEditor.empty()
        for (row in 0..7) {
            parseFigureRow(rows[7 - row], row, editor)
        }
        return editor
    }

    private fun parseFigureRow(figures: String, row: Int, editor: BoardEditor) {
        var line = 0
        for (figure in figures) {
            if (Character.isDigit(figure)) {
                line += (figure - '0')
            } else {
                val piece = parseFigure(figure)
                editor.insertFigure(piece.color, piece.type, Field(line + row * 8))
                ++line
            }
        }
    }

    private fun parseFigure(figure: Char): Piece {
        val color = if(Character.isLowerCase(figure)) FigureColor.BLACK else FigureColor.WHITE
        return when(figure.uppercase()) {
            "P" -> Piece(color, FigureType.PAWN)
            "N" -> Piece(color, FigureType.KNIGHT)
            "B" -> Piece(color, FigureType.BISHOP)
            "R" -> Piece(color, FigureType.ROOK)
            "Q" -> Piece(color, FigureType.QUEEN)
            "K" -> Piece(color, FigureType.KING)
            else -> throw ChessException("Unknown figure $figure")
        }
    }

    private fun parseFlags(castleFlags: String, enPassantFlags: String): Int {
        var ret = 0
        if (castleFlags.contains('k')) {
            ret = ret or Flags.CASTLE_BLACK_KING_SIDE
        }
        if (castleFlags.contains('q')) {
            ret = ret or Flags.CASTLE_BLACK_QUEEN_SIDE
        }
        if (castleFlags.contains('K')) {
            ret = ret or Flags.CASTLE_WHITE_KING_SIDE
        }
        if (castleFlags.contains('Q')) {
            ret = ret or Flags.CASTLE_WHITE_QUEEN_SIDE
        }
        if (enPassantFlags != "-") {
            val line = (enPassantFlags[0] - 'a')
            ret = ret or Flags.enPassant(line)
        }
        return ret
    }

}

fun Board.toFen(totalMoves: Int): String = Fen.boardToString(this, totalMoves)

fun Board.BoardBuilder.parseFen(input: String): Board = Fen.parseBoard(input)
