package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.ChessException
import pluto.chess.MovesAssert
import pluto.chess.board.Board
import pluto.chess.board.Field
import kotlin.test.assertFailsWith

internal class UciSearchOptionsTest {

    @Test
    internal fun testParse_infinite() {
        val options = UciSearchOptions.parse("infinite depth 3 searchmoves e2e4 d2d4 nodes 5")
        assertFalse(options.isSolveForMate)
        assertNull(options.solveForMateMoves)
        assertFalse(options.ponder)
        val result = options.getForBoard(Board.newGame())
        assertNull(result.totalTimeMs)
        assertNull(result.movesUntilNextTimeControl)
        assertEquals(0L, result.timeMsIncreasePerMove)
        assertNotNull(result.allowedMoves)
        result.allowedMoves?.let {
            MovesAssert.assertMoves(it).assertNumberOfMoves(2)
                .assertHasMove(Field.E._2, Field.E._4)
                .assertHasMove(Field.D._2, Field.D._4)
                .assertHasNotMove(Field.G._1, Field.F._3)
        }
        assertEquals(3, result.maxDepth)
        assertEquals(5, result.maxNodes)
    }

    @Test
    internal fun testParse_timeControlTotal() {
        val result = UciSearchOptions.parse("movetime 100").getForBoard(Board.newGame())
        assertEquals(100L, result.totalTimeMs)
        assertNull(result.movesUntilNextTimeControl)
        assertEquals(0L, result.timeMsIncreasePerMove)
        assertNull(result.allowedMoves)
        assertNull(result.maxDepth)
        assertNull(result.maxNodes)
    }

    @Test
    internal fun testParse_timeControlTimeInc() {
        val result = UciSearchOptions.parse("wtime 100 winc 1000 movestogo 5").getForBoard(Board.newGame())
        assertEquals(100L, result.totalTimeMs)
        assertEquals(5, result.movesUntilNextTimeControl)
        assertEquals(1000L, result.timeMsIncreasePerMove)
        assertNull(result.allowedMoves)
        assertNull(result.maxDepth)
        assertNull(result.maxNodes)
    }

    @Test
    internal fun testParse_ponder() {
        val options = UciSearchOptions.parse("infinite ponder")
        assertTrue(options.ponder)
        val result = options.getForBoard(Board.newGame())
        assertNull(result.totalTimeMs)
        assertNull(result.movesUntilNextTimeControl)
        assertEquals(0L, result.timeMsIncreasePerMove)
        assertNull(result.allowedMoves)
        assertNull(result.maxDepth)
        assertNull(result.maxNodes)

    }

    @Test
    internal fun testFails() {
        assertFailsWith<ChessException> { UciSearchOptions.parse("moves e2e4") }
    }
}