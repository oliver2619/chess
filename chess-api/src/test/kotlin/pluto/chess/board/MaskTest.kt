package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

internal class MaskTest {

    @Test
    internal fun testAnd() {
        assertEquals(Mask(0x0100UL), Mask(0x0110UL) and Mask(0x1100UL))
    }

    @Test
    internal fun testOr() {
        assertEquals(Mask(0x1110UL), Mask(0x0110UL) or Mask(0x1100UL))
        assertEquals(Mask(0x101UL), Field.A._1.toMask() or Field.A._2)
        assertEquals(Mask(0x40200UL), Field.B._2.toMask() or Field.C._3)
    }

    @Test
    internal fun testInv() {
        assertEquals(Mask(0xffff_ffff_ffff_ffffUL), Mask().inv())
        assertEquals(Mask(), Mask().inv().inv())
        assertEquals(Mask(0xffff_ffff_ffff_fffeUL), Mask(1UL).inv())
    }

    @Test
    internal fun testCountFields() {
        assertEquals(1, Mask(0x1UL).fieldCount())
        assertEquals(2, Mask(0x6UL).fieldCount())
        assertEquals(4, Mask(0x8000_1000_0010_0001UL).fieldCount())
    }

    @Test
    internal fun testIsEmpty() {
        assertFalse(Mask(0x1UL).isEmpty())
        assertTrue(Mask(0x0UL).isEmpty())
        assertTrue(Mask(0x1UL).isNotEmpty())
        assertFalse(Mask(0x0UL).isNotEmpty())
    }

    @Test
    internal fun testLowestBit() {
        assertEquals(Mask(1UL), Mask(0xf7UL).lowestBit())
        assertEquals(Mask(2UL), Mask(0xf6UL).lowestBit())
        assertEquals(Mask(), Mask().lowestBit())
    }

    @Test
    internal fun testSomeFields_returnTrue() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertTrue(mask.someFields {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            true
        })
        assertEquals(1, cnt)
    }

    @Test
    internal fun testSomeFields_returnFalse() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertFalse(mask.someFields {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            false
        })
        assertEquals(2, cnt)
    }

    @Test
    internal fun testEveryField_returnTrue() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertTrue(mask.everyField {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            true
        })
        assertEquals(2, cnt)
    }

    @Test
    internal fun testEveryField_returnFalse() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertFalse(mask.everyField {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            false
        })
        assertEquals(1, cnt)
    }

    @Test
    internal fun testForEachField_returnContinueAll() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertEquals(MoveConsumerResult.CONTINUE_ALL, mask.forEachField {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            MoveConsumerResult.CONTINUE_ALL
        })
        assertEquals(2, cnt)
    }

    @Test
    internal fun testForEachField_returnContinueValueChanging() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertEquals(MoveConsumerResult.CONTINUE_VALUE_CHANGING, mask.forEachField {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            MoveConsumerResult.CONTINUE_VALUE_CHANGING
        })
        assertEquals(2, cnt)
    }

    @Test
    internal fun testForEachField_returnCancel() {
        val mask = (Field.A._1.toMask() or Field.B._2)
        var cnt = 0
        assertEquals(MoveConsumerResult.CANCEL, mask.forEachField {
            assertTrue(it == Field.A._1 || it == Field.B._2)
            ++cnt
            MoveConsumerResult.CANCEL
        })
        assertEquals(1, cnt)
    }

    @Test
    internal fun testStream() {
        val array = (Field.B._1 or Field.H._8).streamFields().toArray()
        assertEquals(2, array.size)
        assertEquals(Field.B._1, array[0])
        assertEquals(Field.H._8, array[1])
    }

    @Test
    internal fun testReduceFieldsToInt() {
        val mask = (Field.A._3.toMask() or Field.C._5)
        assertEquals(7, mask.reduceFieldsToInt(1) { prev: Int, field: Field ->
            assertTrue(field == Field.A._3 || field == Field.C._5)
            prev + field.row
        })
    }

    @Test
    internal fun testToField() {
        for (i in 0..63) {
            assertEquals(i, Mask(0x1UL shl i).toField().value)
        }
    }
}