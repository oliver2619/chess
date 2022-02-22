package pluto.chess.board

object Flags {

    const val EN_PASSANT_LINE_MASK = 0x7
    const val EN_PASSANT_ENABLED = 0x8
    const val EN_PASSANT = 0xf

    const val CASTLE_WHITE_QUEEN_SIDE = 0x10
    const val CASTLE_WHITE_KING_SIDE = 0x20
    const val CASTLE_WHITE_BOTH = 0x30
    const val CASTLE_BLACK_QUEEN_SIDE = 0x40
    const val CASTLE_BLACK_KING_SIDE = 0x80
    const val CASTLE_BLACK_BOTH = 0xc0
    const val CASTLE_ALL = 0xf0
    const val CASTLE_NONE = 0

    fun enPassant(line: Int) = EN_PASSANT_ENABLED or (line and EN_PASSANT_LINE_MASK)
}