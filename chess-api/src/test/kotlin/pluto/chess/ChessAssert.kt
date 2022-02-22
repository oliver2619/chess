package pluto.chess

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.move.Conversion
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard
import kotlin.test.*

class ChessAssert private constructor(private val board: Board) {

    private val movesWithBoard: List<MoveWithBoard> by lazy { board.findMoves() }

    fun assertHasMove(startField: Field, targetField: Field): ChessAssert {
        assertNotNull(movesWithBoard.find { it.move.startField == startField && it.move.targetField == targetField })
        return this
    }

    fun assertHasNotMove(startField: Field, targetField: Field): ChessAssert {
        assertNull(movesWithBoard.find { it.move.startField == startField && it.move.targetField == targetField })
        return this
    }

    fun assertNumberOfMoves(numberOfMoves: Int): ChessAssert {
        assertEquals(numberOfMoves, movesWithBoard.size)
        return this
    }

    fun move(startField: Field, targetField: Field, conversion: Conversion? = null): ChessAssert {
        val found =
            movesWithBoard.find { it.move.startField == startField && it.move.targetField == targetField && it.move.conversion == conversion }
        assertNotNull(found)
        return ChessAssert(found.board)
    }

    companion object {

        fun assertBoard(board: Board): ChessAssert = ChessAssert(board)
    }
}

class MovesAssert private constructor(private val moves: List<Move>) {

    fun assertHasMove(startField: Field, targetField: Field): MovesAssert {
        assertNotNull(moves.find { it.startField == startField && it.targetField == targetField })
        return this
    }

    fun assertHasNotMove(startField: Field, targetField: Field): MovesAssert {
        assertNull(moves.find { it.startField == startField && it.targetField == targetField })
        return this
    }

    fun assertNumberOfMoves(numberOfMoves: Int): MovesAssert {
        assertEquals(numberOfMoves, moves.size)
        return this
    }

    companion object {
        fun assertMoves(moves: List<Move>): MovesAssert = MovesAssert(moves)
    }
}