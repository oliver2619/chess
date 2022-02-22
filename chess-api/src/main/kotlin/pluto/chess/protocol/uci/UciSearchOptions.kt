package pluto.chess.protocol.uci

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.engine.EngineSearchOptions
import pluto.chess.figure.FigureColor
import pluto.chess.move.Move
import java.util.regex.Pattern

internal class UciSearchOptions {

    val isSolveForMate: Boolean get() = solveMate != null
    val solveForMateMoves: Int? get() = solveMate
    val ponder: Boolean get() = _ponder

    private var infinite: Boolean = false
    private var _ponder: Boolean = false
    private var whiteTime: Int? = null
    private var blackTime: Int? = null
    private var whiteIncPerMove: Int? = null
    private var blackIncPerMove: Int? = null
    private var movesToGo: Int? = null
    private var maxDepth: Int? = null
    private var maxNodes: Int? = null
    private var solveMate: Int? = null
    private var maxTime: Int? = null
    private var searchMoves: List<String>? = null

    fun getForBoard(board: Board): EngineSearchOptions {
        val useTimeControl = useTimeControl()
        return EngineSearchOptions(
            totalTimeMs = getTotalTimeMs(board.colorOnTurn),
            maxDepth = solveMate ?: maxDepth,
            maxNodes = maxNodes,
            allowedMoves = mapMoves(board),
            movesUntilNextTimeControl = if (useTimeControl) movesToGo else null,
            timeMsIncreasePerMove = if (useTimeControl) {
                if (isWhiteOnTurn(board.colorOnTurn)) {
                    whiteIncPerMove?.toLong() ?: 0L
                } else {
                    blackIncPerMove?.toLong() ?: 0L
                }
            } else 0L
        )
    }

    private fun getTotalTimeMs(colorOnTurn: FigureColor): Long? {
        return if (infinite) {
            null
        } else if (maxTime != null) {
            maxTime?.toLong()
        } else {
            if (isWhiteOnTurn(colorOnTurn)) {
                whiteTime?.toLong()
            } else {
                blackTime?.toLong()
            }
        }
    }

    private fun isWhiteOnTurn(colorOnTurn: FigureColor): Boolean = (colorOnTurn == FigureColor.WHITE) xor _ponder

    private fun mapMoves(board: Board): List<Move>? = searchMoves?.map { UciNotation.parse(it, board).move }

    private fun useTimeControl() = !infinite && maxTime == null

    companion object {
        private val INPUT_PATTERN = Pattern.compile("\\s+").toRegex()
        private val MOVE_PATTERN = Pattern.compile("[a-h][1-8][a-h][1-8][nbrq]?").toRegex()

        fun parse(input: String): UciSearchOptions {
            val params = input.split(INPUT_PATTERN)
            val ret = UciSearchOptions()
            var i = 0
            while (i < params.size) {
                when (params[i]) {
                    "searchmoves" -> {
                        ret.searchMoves = mutableListOf<String>().also {
                            while (i + 1 < params.size && params[i + 1].matches(MOVE_PATTERN)) {
                                it.add(params[++i])
                            }
                        }
                    }
                    "infinite" -> ret.infinite = true
                    "ponder" -> ret._ponder = true
                    "wtime" -> ret.whiteTime = params[++i].toInt()
                    "btime" -> ret.blackTime = params[++i].toInt()
                    "winc" -> ret.whiteIncPerMove = params[++i].toInt()
                    "binc" -> ret.blackIncPerMove = params[++i].toInt()
                    "movestogo" -> ret.movesToGo = params[++i].toInt()
                    "depth" -> ret.maxDepth = params[++i].toInt()
                    "nodes" -> ret.maxNodes = params[++i].toInt()
                    "mate" -> ret.solveMate = params[++i].toInt()
                    "movetime" -> ret.maxTime = params[++i].toInt()
                    else -> throw ChessException("Illegal search option '${params[i]}'")
                }
                ++i
            }
            return ret
        }
    }
}