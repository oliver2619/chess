package pluto.chess.figure

import pluto.chess.board.Field
import pluto.chess.board.Ray

internal class Queen(color: FigureColor, field: Field) : RangeFigure(
    color, field, createRangeMoves(
        color, FigureType.QUEEN, field, arrayOf(Ray.N, Ray.NE, Ray.E, Ray.SE, Ray.S, Ray.SW, Ray.W, Ray.NW)
    )
) {

    override val type get() = FigureType.QUEEN
}