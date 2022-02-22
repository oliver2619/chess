package pluto.chess.notation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import pluto.chess.board.BoardEditor
import pluto.chess.board.Field
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.move.Conversion

internal class AlgebraicNotationTest {

    @Test
    internal fun testBoardToString_commonCases() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.A._7)
            .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.C._7)
            .insertFigure(FigureColor.BLACK, FigureType.KNIGHT, Field.B._8)
            .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._2)
            .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._4)
            .resume(FigureColor.WHITE)
        assertEquals("O-O", board.findMove(Field.E._1, Field.G._1)?.move?.toSan(board))
        assertEquals("O-O-O", board.findMove(Field.E._1, Field.C._1)?.move?.toSan(board))
        assertEquals("axb8=Q+", board.findMove(Field.A._7, Field.B._8, Conversion.QUEEN)?.move?.toSan(board))
        assertEquals("cxb8=N", board.findMove(Field.C._7, Field.B._8, Conversion.KNIGHT)?.move?.toSan(board))
        assertEquals("N2d3", board.findMove(Field.B._2, Field.D._3)?.move?.toSan(board))
        assertEquals("N4d3", board.findMove(Field.B._4, Field.D._3)?.move?.toSan(board))
    }

    @Test
    internal fun testBoardToString_fullStartField() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._2)
            .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._4)
            .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.F._2)
            .resume(FigureColor.WHITE)
        assertEquals("Nb2d3", board.findMove(Field.B._2, Field.D._3)?.move?.toSan(board))
    }

    @Test
    internal fun testBoardToString_mate() {
        val board = BoardEditor.withKings(whiteKing = Field.E._6, blackKing = Field.E._8)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._7)
            .resume(FigureColor.WHITE)
        assertEquals("Ra8#", board.findMove(Field.A._7, Field.A._8)?.move?.toSan(board))
    }
}