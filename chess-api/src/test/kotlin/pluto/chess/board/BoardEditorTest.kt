package pluto.chess.board

import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import pluto.chess.ChessException
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.notation.toFen
import kotlin.test.assertFailsWith

internal class BoardEditorTest {

    @Test
    internal fun testCanInsertFigure() {
        assertTrue(BoardEditor.withDefaultKings().canInsertFigure(FigureColor.WHITE, FigureType.PAWN))
        assertTrue(BoardEditor.withDefaultKings().canInsertFigure(FigureColor.WHITE, FigureType.KNIGHT))
        assertFalse(BoardEditor.withDefaultKings().canInsertFigure(FigureColor.WHITE, FigureType.KING))
        assertFalse(BoardEditor.fromStart().canInsertFigure(FigureColor.WHITE, FigureType.PAWN))
        assertFalse(BoardEditor.fromStart().canInsertFigure(FigureColor.WHITE, FigureType.KNIGHT))
        assertTrue(
            BoardEditor.fromStart().removeFigure(Field.E._2).canInsertFigure(FigureColor.WHITE, FigureType.PAWN)
        )
        assertTrue(
            BoardEditor.fromStart().removeFigure(Field.E._2).canInsertFigure(FigureColor.WHITE, FigureType.KNIGHT)
        )
        assertFalse(
            BoardEditor.fromStart().removeFigure(Field.E._2)
                .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.E._2)
                .canInsertFigure(FigureColor.WHITE, FigureType.PAWN)
        )
        assertFalse(
            BoardEditor.fromStart().removeFigure(Field.E._2)
                .insertFigure(FigureColor.WHITE, FigureType.KNIGHT, Field.E._2)
                .canInsertFigure(FigureColor.WHITE, FigureType.KNIGHT)
        )
        assertFalse(
            BoardEditor.fromStart().removeFigure(Field.A._1).canInsertFigure(FigureColor.WHITE, FigureType.KNIGHT)
        )
        assertFalse(
            BoardEditor.fromStart().removeFigure(Field.A._1).canInsertFigure(FigureColor.WHITE, FigureType.PAWN)
        )
    }

    @Test
    internal fun testCanPlaceFigure() {
        assertTrue(BoardEditor.withDefaultKings().canPlaceFigure(FigureType.KNIGHT, Field.A._1))
        assertTrue(BoardEditor.withDefaultKings().canPlaceFigure(FigureType.PAWN, Field.A._2))
        assertFalse(BoardEditor.withDefaultKings().canPlaceFigure(FigureType.PAWN, Field.A._1))
        assertFalse(BoardEditor.withDefaultKings().canPlaceFigure(FigureType.BISHOP, Field.E._1))
    }

    @Test
    internal fun testInsertFigure_ok() {
        val boardEditor = BoardEditor.withDefaultKings()
        assertFalse(boardEditor.isFigureOnField(Field.E._7))
        boardEditor.insertFigure(FigureColor.BLACK, FigureType.PAWN, Field.E._7)
        assertTrue(boardEditor.isFigureOnField(Field.E._7))
        val board = boardEditor.resume(FigureColor.WHITE)
        assertEquals(FigureColor.BLACK, board.getFigureOnField(Field.E._7)?.color)
        assertEquals(FigureType.PAWN, board.getFigureOnField(Field.E._7)?.type)
    }

    @Test
    internal fun testInsertFigure_sameField() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings()
                .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._2)
                .insertFigure(FigureColor.BLACK, FigureType.PAWN, Field.E._2)
        }
    }

    @Test
    internal fun testInsertKing_ok() {
        val board = BoardEditor.empty()
            .insertFigure(FigureColor.WHITE, FigureType.KING, Field.E._1)
            .insertFigure(FigureColor.BLACK, FigureType.KING, Field.E._8)
            .resume(FigureColor.WHITE)
        assertEquals(FigureColor.WHITE, board.getFigureOnField(Field.E._1)?.color)
        assertEquals(FigureType.KING, board.getFigureOnField(Field.E._1)?.type)
        assertEquals(FigureColor.BLACK, board.getFigureOnField(Field.E._8)?.color)
        assertEquals(FigureType.KING, board.getFigureOnField(Field.E._8)?.type)
    }

    @Test
    internal fun testInsertKing_notAllowed() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings()
                .insertFigure(FigureColor.WHITE, FigureType.KING, Field.E._2)
        }
    }

    @Test
    internal fun testInsertFigure_illegalPawnField() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings()
                .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._1)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings()
                .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._8)
        }
    }

    @Test
    internal fun testMoveFigure_ok() {
        val boardEditor = BoardEditor.withDefaultKings().moveFigure(Field.E._1, Field.E._2).resume(FigureColor.WHITE)
        assertTrue(boardEditor.isFigureOnField(Field.E._2))
        assertFalse(boardEditor.isFigureOnField(Field.E._1))
    }

    @Test
    internal fun testMoveFigure2() {
        val board = BoardEditor.fromStart().moveFigure(Field.E._2, Field.E._4).moveFigure(Field.E._7, Field.E._5).resume(FigureColor.WHITE)
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2", board.toFen(2))
    }

    @Test
    internal fun testMoveFigure_fails() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().moveFigure(Field.E._2, Field.E._3)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().moveFigure(Field.E._1, Field.E._8)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.A._2)
                .moveFigure(Field.A._2, Field.A._1)
        }
    }

    @Test
    internal fun testRemoveFigure_ok() {
        val boardEditor = BoardEditor.fromStart()
        assertTrue(boardEditor.isFigureOnField(Field.E._2))
        boardEditor.removeFigure(Field.E._2)
        assertFalse(boardEditor.isFigureOnField(Field.E._2))
    }

    @Test
    internal fun testRemoveFigure_fails() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().removeFigure(Field.E._2)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().removeFigure(Field.E._1)
        }
    }

    @Test
    internal fun testIsFigureOnField() {
        assertTrue(BoardEditor.withDefaultKings().isFigureOnField(Field.E._1))
        assertFalse(BoardEditor.withDefaultKings().isFigureOnField(Field.E._2))
    }

    @Test
    internal fun testGetAvailableCastleFlags() {
        assertEquals(Flags.CASTLE_ALL, BoardEditor.fromStart().getAvailableCastleFlagsToResume())
        assertEquals(Flags.CASTLE_NONE, BoardEditor.withDefaultKings().getAvailableCastleFlagsToResume())
        assertEquals(
            Flags.CASTLE_WHITE_QUEEN_SIDE,
            BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._1)
                .getAvailableCastleFlagsToResume()
        )
        assertEquals(
            Flags.CASTLE_WHITE_KING_SIDE,
            BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
                .getAvailableCastleFlagsToResume()
        )
        assertEquals(
            Flags.CASTLE_BLACK_QUEEN_SIDE,
            BoardEditor.withDefaultKings().insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.A._8)
                .getAvailableCastleFlagsToResume()
        )
        assertEquals(
            Flags.CASTLE_BLACK_KING_SIDE,
            BoardEditor.withDefaultKings().insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.H._8)
                .getAvailableCastleFlagsToResume()
        )
        assertEquals(
            Flags.CASTLE_BLACK_BOTH,
            BoardEditor.fromStart().moveFigure(Field.E._1, Field.E._3).getAvailableCastleFlagsToResume()
        )
    }

    @Test
    internal fun testEnPassantLines() {
        assertEquals(0, BoardEditor.withDefaultKings().getEnPassantLines(FigureColor.WHITE).size)
        val boardEditor = BoardEditor.withDefaultKings()
            .insertFigure(FigureColor.BLACK, FigureType.PAWN, Field.B._5)
            .insertFigure(FigureColor.BLACK, FigureType.PAWN, Field.E._5)
            .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.D._4)
        val linesWhite = boardEditor
            .getEnPassantLines(FigureColor.WHITE)
        assertEquals(2, linesWhite.size)
        assertEquals(Field.B._5.line, linesWhite[0])
        assertEquals(Field.E._5.line, linesWhite[1])
        val linesBlack = boardEditor
            .getEnPassantLines(FigureColor.BLACK)
        assertEquals(1, linesBlack.size)
        assertEquals(Field.D._4.line, linesBlack[0])
    }

    @Test
    internal fun testCanResume() {
        assertTrue(BoardEditor.fromStart().canResumeWithFlags(FigureColor.WHITE, Flags.CASTLE_ALL))
        assertFalse(BoardEditor.fromStart().canResumeWithFlags(FigureColor.WHITE, Flags.enPassant(3)))
        assertTrue(
            BoardEditor.fromStart().moveFigure(Field.E._2, Field.E._4)
                .canResumeWithFlags(FigureColor.BLACK, Flags.enPassant(Field.E._4.line))
        )
        assertFalse(BoardEditor.withDefaultKings().canResumeWithFlags(FigureColor.WHITE, Flags.CASTLE_ALL))
        assertTrue(BoardEditor.withDefaultKings().canResumeWithFlags(FigureColor.WHITE, 0))
    }

    @Test
    internal fun testResume_ok() {
        val boardEditor =
            BoardEditor.withDefaultKings()
                .insertFigure(FigureColor.WHITE, FigureType.PAWN, Field.E._4)
                .insertFigure(FigureColor.BLACK, FigureType.ROOK, Field.A._8)
                .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.H._1)
        val board = boardEditor.resume(
            FigureColor.BLACK,
            boardEditor.getAvailableCastleFlagsToResume() or Flags.enPassant(Field.E._2.line),
            2
        )
        assertNull(board.getFigureOnField(Field.A._2))
        assertNotNull(board.getFigureOnField(Field.E._4))
        assertEquals(FigureColor.WHITE, board.getFigureOnField(Field.E._4)?.color)
        assertEquals(FigureType.PAWN, board.getFigureOnField(Field.E._4)?.type)
        assertEquals(
            Flags.CASTLE_BLACK_QUEEN_SIDE or Flags.CASTLE_WHITE_KING_SIDE or Flags.enPassant(Field.E._2.line),
            board.flags
        )
        assertEquals(FigureColor.BLACK, board.colorOnTurn)
        assertEquals(2, board.fiftyMoves)
    }

    @Test
    internal fun testResume_fails() {
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.E._2)
                .resume(FigureColor.WHITE)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().resume(FigureColor.WHITE, Flags.CASTLE_ALL)
        }
        assertFailsWith<ChessException> {
            BoardEditor.withDefaultKings().resume(FigureColor.WHITE, Flags.CASTLE_NONE, 55)
        }
        assertFailsWith<ChessException> { BoardEditor.empty().resume(FigureColor.WHITE) }
        assertFailsWith<ChessException> { BoardEditor.empty().resume(FigureColor.BLACK) }
    }
}