package pluto.chess.figure

enum class FigureType(private val string: String) {
    PAWN("P"), KNIGHT("N"), BISHOP("B"), ROOK("R"), QUEEN("Q"), KING("K");

    override fun toString(): String = string
}