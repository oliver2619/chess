package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.Conversion
import pluto.chess.move.DefaultMove
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

internal abstract class RangeFigure(color: FigureColor, field: Field, private val moveLists: Array<Array<Move>>) :
    Figure(color, field) {

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? {
        if (conversion != null) {
            return null
        }
        for (moveList in moveLists) {
            for (move in moveList) {
                if (move.isNotBlocked(board)) {
                    if (move.targetField == targetField) {
                        val newBoard = board.moved(move)
                        if (!newBoard.isKingAttacked(color)) {
                            return MoveWithBoard(move, newBoard)
                        }
                        return null
                    }
                    if (move.isCapturing(board)) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return null
    }

    override fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult {
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (moveList in moveLists) {
            for (move in moveList) {
                if (move.isNotBlocked(board)) {
                    val valueChanging = move.isValueChanging(board)
                    if (ret == MoveConsumerResult.CONTINUE_ALL || valueChanging) {
                        val newBoard = board.moved(move)
                        if (!newBoard.isKingAttacked(color)) {
                            ret = ret combine consumer.move(move, newBoard)
                            if (ret == MoveConsumerResult.CANCEL) {
                                return ret
                            }
                        }
                    }
                    if (valueChanging) {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return ret
    }

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean {
        for (directionalMoves in moveLists) {
            for (move in directionalMoves) {
                if (move.isNotBlocked(board)) {
                    if (move.isValueChanging(board)) {
                        val newBoard = board.moved(move)
                        if (!newBoard.isKingAttacked(color)) {
                            if (consumer.move(move, newBoard) == MoveConsumerResult.CANCEL) {
                                return false
                            }
                        }
                        break
                    }
                } else {
                    break
                }
            }
        }
        return true
    }

    companion object {

        @JvmStatic
        protected fun createRangeMoves(
            color: FigureColor,
            type: FigureType,
            startField: Field,
            rays: Array<Ray>
        ): Array<Array<Move>> {
            return rays.map { ray ->
                val ret: Array<Move> = Masks.ray(startField, ray)
                    .streamFields()
                    .map { targetField ->
                        DefaultMove(color, type, startField, targetField)
                    }.sorted(Comparator.comparingInt { it.getManhattanDistance() })
                    .toArray { sz -> arrayOfNulls<Move>(sz) }
                ret
            }.toTypedArray()
        }
    }
}