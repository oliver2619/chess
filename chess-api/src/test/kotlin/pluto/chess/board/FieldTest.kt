package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import pluto.chess.ChessException
import kotlin.test.Test
import kotlin.test.assertFailsWith

internal class FieldTest {

    @Test
    internal fun testRow() {
        assertEquals(1, Field.A._2.row)
        assertEquals(3, Field.E._4.row)
    }

    @Test
    internal fun testLine() {
        assertEquals(0, Field.A._2.line)
        assertEquals(4, Field.E._4.line)
    }

    @Test
    internal fun testToMask() {
        assertEquals(1UL, Field.A._1.toMask().value)
        assertEquals(0x100UL, Field.A._2.toMask().value)
        assertEquals(2UL, Field.B._1.toMask().value)
        assertEquals(0x8000_0000_0000_0000UL, Field.H._8.toMask().value)
    }

    @Test
    internal fun testDistance() {
        assertEquals(1, Field.A._1 manhattan Field.B._1)
        assertEquals(2, Field.A._1 manhattan Field.B._2)
        assertEquals(1, Field.A._1.manhattan(Field.A._2))
        assertEquals(7, Field.E._1.manhattan(Field.E._8))
        assertEquals(7, Field.E._8.manhattan(Field.E._1))
        assertEquals(14, Field.A._1.manhattan(Field.H._8))
        assertEquals(14, Field.H._8.manhattan(Field.A._1))
    }

    @Test
    internal fun testToString() {
        assertEquals("e4", Field.E._4.toString())
    }

    @Test
    internal fun testOr() {
        assertEquals(Mask(3UL), Field.A._1 or Field.B._1)
    }

    @Test
    internal fun testShiftLine() {
        assertEquals(Field.C._2, Field.E._2 shiftLine -2)
        assertEquals(Field.H._4, Field.E._4 shiftLine 3)
        assertFailsWith<ChessException> { Field.D._5 shiftLine -4 }
        assertFailsWith<ChessException> { Field.D._6 shiftLine 5 }
    }

    @Test
    internal fun testShiftRow() {
        assertEquals(Field.C._2, Field.C._4 shiftRow -2)
        assertEquals(Field.H._7, Field.H._4 shiftRow 3)
        assertFailsWith<ChessException> { Field.D._4 shiftRow -4 }
        assertFailsWith<ChessException> { Field.D._6 shiftRow 3 }
    }

    @Test
    internal fun testFields() {
        assertEquals(Field(0), Field.A._1)
        assertEquals(Field(1), Field.B._1)
        assertEquals(Field(8), Field.A._2)
        assertEquals(Field(63), Field.H._8)
    }

    @Test
    internal fun testAtIndex() {
        assertEquals(Field.A._1, Field.atIndex(0))
        assertEquals(Field.B._1, Field.atIndex(1))
        assertEquals(Field.B._2, Field.atIndex(9))
        assertEquals(Field.H._8, Field.atIndex(63))
        assertFailsWith<ChessException> { Field.atIndex(-1) }
        assertFailsWith<ChessException> { Field.atIndex(64) }
    }
}