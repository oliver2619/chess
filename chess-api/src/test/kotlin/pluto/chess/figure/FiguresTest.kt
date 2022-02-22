package pluto.chess.figure

import org.junit.jupiter.api.Assertions.*
import pluto.chess.board.Field
import kotlin.test.Test

internal class FiguresTest{

    @Test
    internal fun testGetFigure() {
        var f = Figures.getFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._1)
        assertEquals(FigureColor.WHITE, f.color)
        assertEquals(FigureType.KNIGHT, f.type)
        assertEquals(Field.B._1, f.field)
        assertEquals("Nb1", f.toString())
        f = Figures.getFigure(FigureColor.BLACK, FigureType.PAWN, Field.E._2)
        assertEquals(FigureColor.BLACK, f.color)
        assertEquals(FigureType.PAWN, f.type)
        assertEquals(Field.E._2, f.field)
        assertEquals("pe2", f.toString())
    }

}