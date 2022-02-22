package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

internal class FlagsTest{

    @Test
    internal fun testFlags() {
        assertEquals(Flags.CASTLE_WHITE_BOTH, Flags.CASTLE_WHITE_KING_SIDE or Flags.CASTLE_WHITE_QUEEN_SIDE)
        assertEquals(Flags.CASTLE_BLACK_BOTH, Flags.CASTLE_BLACK_KING_SIDE or Flags.CASTLE_BLACK_QUEEN_SIDE)
        assertEquals(Flags.CASTLE_ALL, Flags.CASTLE_BLACK_BOTH or Flags.CASTLE_WHITE_BOTH)
        assertEquals(Flags.EN_PASSANT, Flags.EN_PASSANT_ENABLED or Flags.EN_PASSANT_LINE_MASK)
        assertEquals(7, Flags.EN_PASSANT_LINE_MASK)
        assertEquals(Flags.EN_PASSANT_ENABLED or 4, Flags.enPassant(Field.E._4.line))
    }
}