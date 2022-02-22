package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

abstract class Move(
    val figureColor: FigureColor,
    val figureType: FigureType,
    val startField: Field,
    val targetField: Field,
    val conversion: Conversion? = null
) {

    protected val opponentColor = figureColor.inv()

    fun getManhattanDistance() = startField manhattan targetField

    open fun getSeverity(previousBoard: Board, currentBoard: Board): MoveSeverity {
        return if (isValueChanging(previousBoard)) {
            MoveSeverity.VALUE_CHANGING
        } else if (getAttackChange(previousBoard, currentBoard) > 0) {
            MoveSeverity.ATTACKING
        } else {
            MoveSeverity.NULL_MOVE
        }
    }

    abstract fun isCapturing(board: Board): Boolean

    abstract fun isNotBlocked(board: Board): Boolean

    abstract fun isValueChanging(board: Board): Boolean

    abstract fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>)

    override fun toString() = "$startField-$targetField:${conversion?.figure ?: ""}"

    private fun getAttackChange(previousBoard: Board, currentBoard: Board): Int {
        TODO("Not implemented")
    }

    companion object {

        @JvmStatic
        protected fun calculateClearFlags(startField: Field, targetField: Field): Int {
            var ret = Flags.EN_PASSANT.inv()
            if (startField == Field.A._1 || targetField == Field.A._1) {
                ret = ret and Flags.CASTLE_WHITE_QUEEN_SIDE.inv()
            }
            if (startField == Field.H._1 || targetField == Field.H._1) {
                ret = ret and Flags.CASTLE_WHITE_KING_SIDE.inv()
            }
            if (startField == Field.E._1) {
                ret = ret and Flags.CASTLE_WHITE_BOTH.inv()
            }
            if (startField == Field.A._8 || targetField == Field.A._8) {
                ret = ret and Flags.CASTLE_BLACK_QUEEN_SIDE.inv()
            }
            if (startField == Field.H._8 || targetField == Field.H._8) {
                ret = ret and Flags.CASTLE_BLACK_KING_SIDE.inv()
            }
            if (startField == Field.E._8) {
                ret = ret and Flags.CASTLE_BLACK_BOTH.inv()
            }
            return ret
        }
    }
}