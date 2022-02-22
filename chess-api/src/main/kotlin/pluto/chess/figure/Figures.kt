package pluto.chess.figure

import pluto.chess.board.Field

internal object Figures {

    private val FIGURES: Array<Array<Array<Figure>>> = FigureColor.values().map { color ->
        FigureType.values().map { type ->
            Array(64) { field ->
                createFigure(color, type, Field(field))
            }
        }.toTypedArray()
    }.toTypedArray()

    fun getFigure(color: FigureColor, type: FigureType, field: Field): Figure {
        return FIGURES[color.ordinal][type.ordinal][field.value]
    }

    private fun createFigure(color: FigureColor, type: FigureType, field: Field): Figure {
        return when(type) {
            FigureType.PAWN -> Pawn.create(color, field)
            FigureType.KNIGHT -> Knight(color, field)
            FigureType.BISHOP -> Bishop(color, field)
            FigureType.ROOK -> Rook(color, field)
            FigureType.QUEEN -> Queen(color, field)
            FigureType.KING -> King(color, field)
        }
    }
}
