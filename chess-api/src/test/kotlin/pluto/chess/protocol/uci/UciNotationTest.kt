package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.board.Board
import pluto.chess.board.BoardEditor
import pluto.chess.board.Field
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.move.Conversion
import java.lang.RuntimeException

internal class UciNotationTest {

    @Test
    internal fun testParse() {
        val move = UciNotation.parse("b1c3", Board.newGame()).move
        assertEquals(Field.B._1, move.startField)
        assertEquals(Field.C._3, move.targetField)
        assertEquals(FigureType.KNIGHT, move.figureType)
    }

    @Test
    internal fun testParseConversion() {
        val board = BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.A._7)
            .resume(FigureColor.WHITE)
        val move = UciNotation.parse("a7a8q", board).move
        assertEquals(Field.A._7, move.startField)
        assertEquals(Field.A._8, move.targetField)
        assertEquals(Conversion.QUEEN, move.conversion)
        assertEquals(FigureType.PAWN, move.figureType)
    }

    @Test
    internal fun testToString() {
        val board = BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.A._7)
            .resume(FigureColor.WHITE)
        val move = board.findMove(Field.A._7, Field.A._8, Conversion.QUEEN)?.move?: throw RuntimeException()
        val move2 = board.findMove(Field.E._1, Field.E._2)?.move?: throw RuntimeException()
        assertEquals("a7a8q", UciNotation.toString(move))
        assertEquals("e1e2", UciNotation.toString(move2))
    }
}