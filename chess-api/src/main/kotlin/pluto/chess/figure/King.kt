package pluto.chess.figure

import pluto.chess.board.Field
import pluto.chess.board.Masks
import pluto.chess.move.CastleMove
import pluto.chess.move.Move

internal class King(color: FigureColor, field: Field) : NearFigure(color, field, createKingMoves(color, field)) {

    override val type get() = FigureType.KING

    companion object {
        private fun createKingMoves(color: FigureColor, startField: Field): Array<Move> {
            return if ((startField == Field.E._1 && color == FigureColor.WHITE) || (startField == Field.E._8 && color == FigureColor.BLACK)) {
                val ret = mutableListOf<Move>()
                createMoves(color, FigureType.KING, startField, Masks.kingAttack(startField), ret)
                ret.add(CastleMove(color, startField, startField shiftLine -2))
                ret.add(CastleMove(color, startField, startField shiftLine 2))
                ret.toTypedArray()
            } else {
                createMoves(color, FigureType.KING, startField, Masks.kingAttack(startField))
            }
        }
    }
}