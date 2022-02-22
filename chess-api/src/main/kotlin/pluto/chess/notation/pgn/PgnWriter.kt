package pluto.chess.notation.pgn

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.GameResult
import pluto.chess.figure.FigureColor
import pluto.chess.move.Move
import pluto.chess.notation.toFen
import pluto.chess.notation.toSan
import java.io.Writer

interface PgnMoveWriter {

    fun move(move: Move, comment: String? = null): PgnMoveWriter

    fun end()

    fun next(metaData: PgnMetaData): PgnWriter
}

class PgnWriter(private val metaData: PgnMetaData, private val writer: Writer) {

    private var board: Board? = null
    private var totalMoves = 0
    private var phase = PHASE_TAGS
    private var hasMoveIndex = false

    fun setBoard(board: Board, totalMoves: Int?): PgnWriter {
        if (phase != PHASE_TAGS) {
            throw ChessException("Initial board can not be reset again")
        }
        this.board = board
        this.totalMoves = totalMoves ?: 0
        return this
    }

    fun moves(): PgnMoveWriter {
        if (phase == PHASE_TAGS) {
            writeTags()
            phase = PHASE_MOVES
        }
        if (phase != PHASE_MOVES) {
            throw ChessException("No more moves can be appended to a finished game")
        }
        if (board == null) {
            board = Board.newGame()
        }
        return object : PgnMoveWriter {

            override fun move(move: Move, comment: String?): PgnMoveWriter {
                if (phase != PHASE_MOVES) {
                    throw ChessException("No more moves can be appended to a finished game")
                }
                board = board?.let {
                    it.findMove(move.startField, move.targetField, move.conversion)
                        ?: throw ChessException("Illegal move $move")
                    if (it.colorOnTurn == FigureColor.WHITE) {
                        writer.append((totalMoves / 2 + 1).toString()).append(". ")
                    } else if (!hasMoveIndex) {
                        writer.append((totalMoves / 2 + 1).toString()).append("... ")
                    }
                    ++totalMoves
                    hasMoveIndex = true
                    writer.append(move.toSan(it)).append(" ")
                    it.moved(move)
                }
                if (comment != null) {
                    writeComment(comment)
                }
                return this
            }

            override fun end() {
                if (phase != PHASE_END) {
                    writer.write(gameResultToString(metaData.result))
                    phase = PHASE_END
                } else {
                    throw ChessException("Game has already finished")
                }
            }

            override fun next(metaData: PgnMetaData): PgnWriter {
                return PgnWriter(metaData, writer)
            }
        }
    }

    private fun writeTag(name: String, value: String) {
        writer.append('[')
            .append(name)
            .append(" \"")
            .append(value.replace("\\", "\\\\").replace("\"", "\\\""))
            .append("\"]\n")
    }

    private fun writeTags() {
        writeTag("Event", metaData.event)
        writeTag("Site", metaData.site)
        writeTag("Date", metaData.dateString)
        writeTag("Round", metaData.roundString)
        writeTag("White", metaData.white)
        writeTag("Black", metaData.black)
        writeTag("Result", gameResultToString(metaData.result))
        board?.let {
            writeTag("SetUp", "1")
            writeTag("FEN", it.toFen(totalMoves))
        }
        for (e in metaData.getDataEntries()) {
            writeTag(e.key, e.value)
        }
    }

    private fun writeComment(comment: String) {
        writer.append('{').append(comment.replace('}', ')')).append("} ")
    }

    private fun gameResultToString(gameResult: GameResult): String = when (gameResult) {
        GameResult.WHITE_WON -> "1-0"
        GameResult.BLACK_WON -> "0-1"
        GameResult.REMIS -> "1/2-1/2"
        GameResult.UNDEFINED -> "*"
    }

    companion object {
        private const val PHASE_TAGS = 0
        private const val PHASE_MOVES = 1
        private const val PHASE_END = 2
    }
}