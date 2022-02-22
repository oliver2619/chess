package pluto.chess.engine.ai

import pluto.chess.board.Board
import pluto.chess.board.MoveConsumerResult

class MinMax(private val rater: BoardRater) : BoardRater {

    // TODO caching depth 4+

    override fun rate(board: Board): Int {
        return rate(board, BoardRater.MIN_VALUE, BoardRater.MAX_VALUE, 0, HashMap())
    }

    private fun rate(board: Board, alpha: Int, beta: Int, depth: Int, cache: Map<Board, Any>): Int {
        if (board.isRemisByMaterialOrFiftyMoves()) {
            return 0
        }
        var hasMoves = false
        var maxRating = BoardRater.MIN_VALUE
        var curAlpha = alpha
        val foundMoves = board.findMoves { move, newBoard ->
            hasMoves = true
            val valueChanging = move.isValueChanging(board)
            val rating = if (valueChanging) {
                -rate(newBoard, -beta, -curAlpha, depth + 1, cache)
            } else {
                rater.rate(board)
            }
            if (rating > beta) {
                maxRating = rating
                return@findMoves MoveConsumerResult.CANCEL
            }
            if (rating > maxRating) {
                maxRating = rating
                if (rating > curAlpha) {
                    curAlpha = rating
                }
            }
            if (valueChanging) MoveConsumerResult.CONTINUE_ALL else MoveConsumerResult.CONTINUE_VALUE_CHANGING
        }
        if (!foundMoves) {
            return maxRating
        }
        if (!hasMoves) {
            return if (board.isChecked()) BoardRater.MIN_VALUE + depth else 0
        }
        return maxRating
    }
}