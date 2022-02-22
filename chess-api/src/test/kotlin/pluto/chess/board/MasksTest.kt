package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import pluto.chess.figure.FigureColor
import kotlin.test.Test

internal class MasksTest {

    @Test
    internal fun testLine() {
        assertEquals(Mask(0x8000_0000_0000_0000UL), Masks.line(7) and Field.H._8.toMask())
        assertEquals(
            Field.E._1.toMask() or Field.E._2 or Field.E._3 or Field.E._4
                    or Field.E._5 or Field.E._6 or Field.E._7 or Field.E._8,
            Masks.line(4)
        )
    }

    @Test
    internal fun testRow() {
        assertEquals(
            Field.A._4.toMask() or Field.B._4 or Field.C._4 or Field.D._4
                    or Field.E._4 or Field.F._4 or Field.G._4 or Field.H._4,
            Masks.row(3)
        )
        assertEquals(Mask(0x8000_0000_0000_0000UL), Masks.row(7) and Field.H._8.toMask())
    }

    @Test
    internal fun testRay() {
        assertEquals(Field.A._3.toMask() or Field.B._3, Masks.ray(Field.C._3, Ray.W))
        assertEquals(Field.A._1.toMask() or Field.B._2, Masks.ray(Field.C._3, Ray.SW))
        assertEquals(Field.C._1.toMask() or Field.C._2, Masks.ray(Field.C._3, Ray.S))
        assertEquals(Field.E._1.toMask() or Field.D._2, Masks.ray(Field.C._3, Ray.SE))
        assertEquals(Field.G._6.toMask() or Field.H._6, Masks.ray(Field.F._6, Ray.E))
        assertEquals(Field.G._7.toMask() or Field.H._8, Masks.ray(Field.F._6, Ray.NE))
        assertEquals(Field.F._7.toMask() or Field.F._8, Masks.ray(Field.F._6, Ray.N))
        assertEquals(Field.E._7.toMask() or Field.D._8, Masks.ray(Field.F._6, Ray.NW))
    }

    @Test
    internal fun testPawnAttack() {
        assertEquals(Field.D._5.toMask() or Field.F._5, Masks.pawnAttack(Field.E._4, FigureColor.WHITE))
        assertEquals(Field.B._5.toMask(), Masks.pawnAttack(Field.A._4, FigureColor.WHITE))
        assertEquals(Field.D._4.toMask() or Field.F._4, Masks.pawnAttack(Field.E._5, FigureColor.BLACK))
        assertEquals(Field.G._4.toMask(), Masks.pawnAttack(Field.H._5, FigureColor.BLACK))
    }

    @Test
    internal fun testKnightAttack() {
        assertEquals(8, Masks.knightAttack(Field.E._4).fieldCount())
        assertEquals(2, Masks.knightAttack(Field.A._1).fieldCount())
        assertEquals(3, Masks.knightAttack(Field.A._2).fieldCount())
        assertEquals(4, Masks.knightAttack(Field.B._2).fieldCount())
        assertEquals(6, Masks.knightAttack(Field.C._2).fieldCount())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.D._2.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.F._2.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.C._3.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.G._3.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.C._5.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.G._5.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.D._6.toMask()).isNotEmpty())
        assertTrue((Masks.knightAttack(Field.E._4) and Field.F._6.toMask()).isNotEmpty())
    }

    @Test
    internal fun testKingAttack() {
        assertEquals(8, Masks.kingAttack(Field.E._4).fieldCount())
        assertEquals(3, Masks.kingAttack(Field.A._1).fieldCount())
        assertEquals(5, Masks.kingAttack(Field.A._2).fieldCount())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.E._3.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.E._5.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.D._3.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.D._4.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.D._5.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.F._3.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.F._4.toMask()).isNotEmpty())
        assertTrue((Masks.kingAttack(Field.E._4) and Field.F._5.toMask()).isNotEmpty())
    }

    @Test
    internal fun testFigureAttack() {
        assertEquals(Masks.ray(Field.E._4, Ray.NE) or Masks.ray(Field.E._4, Ray.NW) or Masks.ray(Field.E._4, Ray.SE) or Masks.ray(
            Field.E._4,
            Ray.SW
        ), Masks.bishopAttack(Field.E._4))
        assertEquals(13, Masks.bishopAttack(Field.E._4).fieldCount())
        assertEquals(Masks.ray(Field.E._4, Ray.N) or Masks.ray(Field.E._4, Ray.E) or Masks.ray(Field.E._4, Ray.S) or Masks.ray(
            Field.E._4,
            Ray.W
        ), Masks.rookAttack(Field.E._4))
        assertEquals(14, Masks.rookAttack(Field.E._4).fieldCount())
        assertEquals(Masks.bishopAttack(Field.E._4) or Masks.rookAttack(Field.E._4), Masks.queenAttack(Field.E._4))
        assertEquals(27, Masks.queenAttack(Field.E._4).fieldCount())
    }
}