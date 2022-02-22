package pluto.chess.notation

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.board.Board
import pluto.chess.board.Field

internal class FenTest{

    @Test
    internal fun testToString() {
        val board1 = Board.newGame()
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", board1.toFen(0))
        val board2 = board1.findMove(Field.E._2, Field.E._4)?.board
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board2?.toFen(1))
        val board3 = board2?.findMove(Field.C._7, Field.C._5)?.board
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2", board3?.toFen(2))
        val board4 = board3?.findMove(Field.G._1, Field.F._3)?.board
        assertEquals("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", board4?.toFen(3))
    }

    @Test
    internal fun testRoundtrip() {
        val fen = "rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"
        assertEquals(fen, Board.parseFen(fen).toFen(2))
    }
}