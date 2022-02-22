package pluto.chess.engine.ai

import pluto.chess.board.Board

interface BoardRater {

    fun rate(board: Board): Int

    companion object {

        val MIN_VALUE = -Integer.MAX_VALUE + 10_000
        val MAX_VALUE = Integer.MAX_VALUE - 10_000
    }
}