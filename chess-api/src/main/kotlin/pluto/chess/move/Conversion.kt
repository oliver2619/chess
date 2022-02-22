package pluto.chess.move

import pluto.chess.figure.FigureType

enum class Conversion(val figure: FigureType) {
    QUEEN(FigureType.QUEEN), KNIGHT(FigureType.KNIGHT), ROOK(FigureType.ROOK), BISHOP(FigureType.BISHOP);

    override fun toString(): String = figure.toString()

    companion object {
        fun forFigure(figureType: FigureType): Conversion? {
            return enumValues<Conversion>().find { it.figure == figureType }
        }
    }
}