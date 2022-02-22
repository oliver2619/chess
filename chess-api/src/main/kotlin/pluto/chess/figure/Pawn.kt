package pluto.chess.figure

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.MoveConsumer
import pluto.chess.board.MoveConsumerResult
import pluto.chess.move.Conversion
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

internal abstract class Pawn(color: FigureColor, field: Field) : Figure(color, field) {

    override val type get() = FigureType.PAWN

    companion object {
        fun create(color: FigureColor, field: Field): Figure =
            when (field.row) {
                0, 7 -> IllegalPawn(color, field)
                1 -> if (color == FigureColor.WHITE) StartPawn(color, field) else ConvertingPawn(color, field)
                6 -> if (color == FigureColor.BLACK) StartPawn(color, field) else ConvertingPawn(color, field)
                else -> MiddlePawn(color, field)
            }
    }
}

private class IllegalPawn(color: FigureColor, field: Field) : Pawn(color, field) {

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? = null

    override fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult = MoveConsumerResult.CONTINUE_ALL

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean = true
}
