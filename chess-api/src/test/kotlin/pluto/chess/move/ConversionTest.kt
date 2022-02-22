package pluto.chess.move

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.figure.FigureType

internal class ConversionTest {

    @Test
    internal fun testForFigure() {
        assertEquals(Conversion.KNIGHT, Conversion.forFigure(FigureType.KNIGHT))
        assertEquals(Conversion.QUEEN, Conversion.forFigure(FigureType.QUEEN))
        assertEquals(Conversion.ROOK, Conversion.forFigure(FigureType.ROOK))
        assertEquals(Conversion.BISHOP, Conversion.forFigure(FigureType.BISHOP))
        assertNull(Conversion.forFigure(FigureType.PAWN))
    }
}