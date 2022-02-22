package pluto.chess.figure

import pluto.chess.board.Field
import pluto.chess.board.Ray

internal class Rook(color: FigureColor, field: Field) : RangeFigure(
    color, field, createRangeMoves(color, FigureType.ROOK, field, arrayOf(Ray.N, Ray.S, Ray.E, Ray.W))
) {

    override val type get() = FigureType.ROOK
}