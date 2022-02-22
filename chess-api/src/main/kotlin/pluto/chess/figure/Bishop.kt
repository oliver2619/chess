package pluto.chess.figure

import pluto.chess.board.Field
import pluto.chess.board.Ray

internal class Bishop(color: FigureColor, field: Field) : RangeFigure(
    color, field, createRangeMoves(
        color, FigureType.BISHOP, field, arrayOf(Ray.NE, Ray.SE, Ray.NW, Ray.SW)
    )
) {

    override val type get() = FigureType.BISHOP
}