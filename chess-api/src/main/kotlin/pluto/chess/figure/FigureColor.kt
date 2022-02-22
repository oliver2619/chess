package pluto.chess.figure

enum class FigureColor {
    WHITE, BLACK;

    fun inv() = enumValues<FigureColor>()[1 - this.ordinal]
}