package pluto.chess.figure

import pluto.chess.board.Field
import pluto.chess.board.Masks

internal class Knight(color: FigureColor, field: Field) :
    NearFigure(color, field, createMoves(color, FigureType.KNIGHT, field, Masks.knightAttack(field))) {

    override val type get() = FigureType.KNIGHT
}