package pluto.chess.engine.ai

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.engine.EngineThinkLine
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard
import java.util.concurrent.atomic.AtomicIntegerArray
import kotlin.random.Random

class ThinkingTree(private val rater: BoardRater) {

    private val minMax = MinMax(rater)
    private var root: Element? = null

    fun reset() {
        root = null
    }

    fun think(board: Board, active: () -> Boolean): EngineThinkLine {
        root = root?.findElement(board) ?: Element(board)
        return root!!.let {
            it.rateAllImmediately(rater)
            while (active() && !it.isFinished) {
                it.think(minMax)
            }
            val moves = ArrayList<MoveWithBoard>()
            it.getBestMoves(moves)
            return EngineThinkLine(moves, it.rating)
        }
    }
}

private class Element(val board: Board) {

    private var children: MutableList<MoveWithWeight>? = null
    private var _rating = 0
    private var finished = false
    private var isSearching = false

    val isFinished: Boolean get() = finished

    val rating: Int get() = _rating

    fun findElement(board: Board): Element? {
        if (this.board == board) {
            return this
        }
        if (children != null && !isSearching) {
            isSearching = true
            val found = children!!.map { it.findElement(board) }.filterNotNull().firstOrNull()
            isSearching = false
            return found
        } else {
            return null
        }
    }

    fun getBestMoves(moves: MutableList<MoveWithBoard>) {
        if (children?.isEmpty() != false) {
            throw ChessException("No moves have been examined")
        }
        children!!.let { ch ->
            ch.sortWith(Comparator.comparingInt { child -> -child.rating })
            val maxRating = ch[0].rating
            val bestChildren = ch.filter { it.rating == maxRating }
            val bestChild = bestChildren[Random.nextInt(bestChildren.size)]
            moves.add(MoveWithBoard(bestChild.move, bestChild.board))
            bestChild.getBestMoves(moves)
        }
    }

    fun think(minMax: MinMax) {

    }

    fun rateAllImmediately(rater: BoardRater){

    }
}

private class MoveWithWeight(private val element: Element, val move: Move) {

    val board: Board get() = element.board

    val rating: Int get() = element.rating

    fun findElement(board: Board): Element? = element.findElement(board)

    fun getBestMoves(moves: MutableList<MoveWithBoard>) = element.getBestMoves(moves)
}