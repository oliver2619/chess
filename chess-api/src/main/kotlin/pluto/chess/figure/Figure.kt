package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.Conversion
import pluto.chess.move.DefaultMove
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

abstract class Figure(val color: FigureColor, val field: Field) {

    abstract val type: FigureType

    internal abstract fun findMove(board: Board, targetField: Field, conversion: Conversion? = null): MoveWithBoard?

    internal abstract fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult

    internal abstract fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean

    override fun toString(): String =
        "${if (color == FigureColor.WHITE) type.toString().uppercase() else type.toString().lowercase()}$field"

    companion object {

        @JvmStatic
        protected fun createMoves(
            color: FigureColor,
            type: FigureType,
            startField: Field,
            targetMask: Mask
        ): Array<Move> {
            return targetMask.streamFields()
                .map { targetField -> DefaultMove(color, type, startField, targetField) }
                .toArray { arrayOfNulls<Move>(it) }
        }

        @JvmStatic
        protected fun createMoves(
            color: FigureColor,
            type: FigureType,
            startField: Field,
            targetMask: Mask,
            moves: MutableList<Move>
        ) {
            targetMask.forEachField {
                moves.add(DefaultMove(color, type, startField, it))
                MoveConsumerResult.CONTINUE_ALL
            }
        }
    }
}