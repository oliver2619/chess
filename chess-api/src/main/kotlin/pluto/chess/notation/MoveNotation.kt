package pluto.chess.notation

import pluto.chess.board.Board
import pluto.chess.move.Move

fun interface MoveNotation {

    fun moveToString(move: Move, board: Board): String
}