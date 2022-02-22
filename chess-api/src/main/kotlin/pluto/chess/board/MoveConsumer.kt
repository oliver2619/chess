package pluto.chess.board

import pluto.chess.move.Move

fun interface MoveConsumer {

    fun move(move: Move, board: Board): MoveConsumerResult
}