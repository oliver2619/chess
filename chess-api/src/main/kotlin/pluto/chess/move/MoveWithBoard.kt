package pluto.chess.move

import pluto.chess.board.Board

data class MoveWithBoard(val move: Move, val board: Board) {

    override fun toString(): String = move.toString()
}