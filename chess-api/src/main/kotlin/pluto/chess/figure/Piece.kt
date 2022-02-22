package pluto.chess.figure

data class Piece(val color: FigureColor, val type: FigureType) {

    override fun toString(): String =
        if (color == FigureColor.WHITE) type.toString().uppercase() else type.toString().lowercase()
}