package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import pluto.chess.ChessAssert
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import java.lang.RuntimeException
import kotlin.test.Test

// TODO test castle move result
// TODO test usual moves
internal class BoardTest {

    @Test
    internal fun testNewGame() {
        val board = Board.newGame()
        assertEquals(FigureColor.WHITE, board.colorOnTurn)
        assertEquals(Flags.CASTLE_ALL, board.flags)
        assertEquals(0, board.fiftyMoves)
        assertEquals(Masks.row(0) or Masks.row(1) or Masks.row(6) or Masks.row(7), board.getColorMask())
        assertEquals(Masks.row(1) or Masks.row(6), board.getFigureMask(FigureType.PAWN))
    }

    @Test
    internal fun testIsFigureOnField() {
        val board = Board.newGame()
        assertFalse(board.isFigureOnField(FigureColor.WHITE, Field.E._4))
        assertFalse(board.isFigureOnField(FigureColor.BLACK, Field.E._4))
        assertFalse(board.isFigureOnField(Field.E._4))
        assertTrue(board.isFigureOnField(FigureColor.WHITE, Field.E._1))
        assertFalse(board.isFigureOnField(FigureColor.BLACK, Field.E._1))
        assertTrue(board.isFigureOnField(Field.E._1))
        assertTrue(board.isFigureOnField(FigureColor.BLACK, Field.E._8))
        assertFalse(board.isFigureOnField(FigureColor.WHITE, Field.E._8))
        assertTrue(board.isFigureOnField(Field.E._8))
    }

    @Test
    internal fun testGetFigure() {
        val board = Board.newGame()
        assertNull(board.getFigureOnField(Field.E._4))
        var f = board.getFigureOnField(Field.E._1)
        assertEquals(FigureType.KING, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.D._1)
        assertEquals(FigureType.QUEEN, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.C._1)
        assertEquals(FigureType.BISHOP, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.B._1)
        assertEquals(FigureType.KNIGHT, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.A._1)
        assertEquals(FigureType.ROOK, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.A._2)
        assertEquals(FigureType.PAWN, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.F._1)
        assertEquals(FigureType.BISHOP, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.G._1)
        assertEquals(FigureType.KNIGHT, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.H._1)
        assertEquals(FigureType.ROOK, f?.type)
        assertEquals(FigureColor.WHITE, f?.color)
        f = board.getFigureOnField(Field.H._8)
        assertEquals(FigureType.ROOK, f?.type)
        assertEquals(FigureColor.BLACK, f?.color)
    }

    @Test
    internal fun testIsFieldAttacked_case1() {
        val board = BoardEditor.withKings(Field.E._1, Field.B._8)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._8)
            .insertFigure(FigureColor.WHITE, FigureType.BISHOP, Field.F._8)
            .resume(FigureColor.WHITE)
        assertTrue(board.isFieldAttackedBy(Field.G._8, FigureColor.WHITE))
        assertTrue(board.isFieldAttackedBy(Field.F._8, FigureColor.WHITE))
        assertFalse(board.isFieldAttackedBy(Field.E._8, FigureColor.WHITE))
    }

    @Test
    internal fun testIsFieldAttacked_case2() {
        val board = BoardEditor.withKings(Field.E._1, Field.B._8)
            .insertFigure(FigureColor.BLACK, FigureType.BISHOP, Field.F._8)
            .resume(FigureColor.WHITE)
        assertFalse(board.isFieldAttackedBy(Field.G._8, FigureColor.WHITE))
        assertFalse(board.isFieldAttackedBy(Field.E._8, FigureColor.WHITE))
    }

    @Test
    internal fun testIsFieldAttacked_case3() {
        val board = BoardEditor.withKings(Field.E._1, Field.H._8)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._8)
            .insertFigure(FigureColor.BLACK, FigureType.BISHOP, Field.C._8)
            .resume(FigureColor.WHITE)
        assertTrue(board.isFieldAttackedBy(Field.B._8, FigureColor.WHITE))
        assertTrue(board.isFieldAttackedBy(Field.C._8, FigureColor.WHITE))
        assertFalse(board.isFieldAttackedBy(Field.D._8, FigureColor.WHITE))
        assertFalse(board.isFieldAttackedBy(Field.H._8, FigureColor.WHITE))
    }

    @Test
    internal fun testIsFieldAttacked_case4() {
        val board = BoardEditor.withKings(Field.E._1, Field.H._8)
            .insertFigure(FigureColor.BLACK, FigureType.BISHOP, Field.C._8)
            .resume(FigureColor.WHITE)
        assertFalse(board.isFieldAttackedBy(Field.B._8, FigureColor.WHITE))
        assertFalse(board.isFieldAttackedBy(Field.H._8, FigureColor.WHITE))
    }

    @Test
    internal fun testGetMoves_newGame() {
        val board = Board.newGame()

        ChessAssert.assertBoard(board)
            .assertNumberOfMoves(20)
            .assertHasMove(Field.D._2, Field.D._4)
            .assertHasMove(Field.D._2, Field.D._3)
            .assertHasMove(Field.E._2, Field.E._4)
            .assertHasMove(Field.E._2, Field.E._3)
            .assertHasMove(Field.B._1, Field.C._3)
            .assertHasMove(Field.G._1, Field.F._3)
            .assertHasNotMove(Field.E._1, Field.F._1)
    }

    @Test
    internal fun testGetMoves_castleOk() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .resume(FigureColor.WHITE, Flags.CASTLE_WHITE_BOTH)

        ChessAssert.assertBoard(board)
            .assertHasMove(Field.E._1, Field.G._1)
            .assertHasMove(Field.E._1, Field.C._1)
    }

    @Test
    internal fun testGetMoves_castleFails_checked() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.E._7)
            .resume(FigureColor.WHITE, Flags.CASTLE_WHITE_BOTH)

        ChessAssert.assertBoard(board)
            .assertHasNotMove(Field.E._1, Field.G._1)
            .assertHasNotMove(Field.E._1, Field.C._1)
    }

    @Test
    internal fun testGetMoves_castleFails_attackedField() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.D._8)
            .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.F._8)
            .resume(FigureColor.WHITE, Flags.CASTLE_WHITE_BOTH)

        ChessAssert.assertBoard(board)
            .assertHasNotMove(Field.E._1, Field.G._1)
            .assertHasNotMove(Field.E._1, Field.C._1)
    }

    @Test
    internal fun testGetMoves_castleFails_checkedAfter() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.C._8)
            .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.G._8)
            .resume(FigureColor.WHITE, Flags.CASTLE_WHITE_BOTH)

        ChessAssert.assertBoard(board)
            .assertHasNotMove(Field.E._1, Field.G._1)
            .assertHasNotMove(Field.E._1, Field.C._1)
    }

    @Test
    internal fun testGetMoves_castleFails_alreadyMoved() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
            .resume(FigureColor.WHITE, 0)

        ChessAssert.assertBoard(board)
            .assertHasNotMove(Field.E._1, Field.G._1)
            .assertHasNotMove(Field.E._1, Field.C._1)
    }

    @Test
    internal fun testGetMoves_enPassant() {
        val board = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._2)
            .insertFigure(FigureColor.BLACK, FigureType.PAWN, Field.D._4)
            .resume(FigureColor.WHITE)
        val found1 = board.findMove(Field.E._2, Field.E._4) ?: throw RuntimeException()
        ChessAssert.assertBoard(found1.board).assertHasMove(Field.D._4, Field.E._3)
        val found2 = found1.board.findMove(Field.D._4, Field.E._3) ?: throw RuntimeException()
        assertNull(found2.board.getFigureOnField(Field.E._4))
    }

    @Test
    internal fun testRemisByMoves() {
        val boardInsuff = BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.B._1)
            .insertFigure(FigureColor.BLACK, FigureType.BISHOP, Field.C._8).resume(FigureColor.WHITE)
        val boardSuff = BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._2)
            .resume(FigureColor.WHITE)
        assertFalse(boardInsuff.hasSufficientMaterial())
        assertTrue(boardInsuff.isRemisByMaterialOrFiftyMoves())
        assertTrue(boardSuff.hasSufficientMaterial())
        assertFalse(boardSuff.isRemisByMaterialOrFiftyMoves())
    }
}