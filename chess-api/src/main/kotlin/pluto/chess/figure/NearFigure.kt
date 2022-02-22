package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.Conversion
import pluto.chess.move.DefaultMove
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

internal abstract class NearFigure(color: FigureColor, field: Field, private val moves: Array<Move>) :
    Figure(color, field) {

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? {
        if (conversion != null) {
            return null
        }
        for (move in moves) {
            if (move.targetField == targetField) {
                if (move.isNotBlocked(board)) {
                    val newBoard = board.moved(move)
                    if (!newBoard.isKingAttacked(color)) {
                        return MoveWithBoard(move, newBoard)
                    }
                }
                return null
            }
        }
        return null
    }

    override fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult {
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (move in moves) {
            if (move.isNotBlocked(board) && (ret == MoveConsumerResult.CONTINUE_ALL || move.isValueChanging(board))) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    ret = ret combine consumer.move(move, newBoard)
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                }
            }
        }
        return ret
    }

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean {
        for (move in moves) {
            if (move.isNotBlocked(board) && move.isValueChanging(board)) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    if (consumer.move(move, newBoard) == MoveConsumerResult.CANCEL) {
                        return false
                    }
                }
            }
        }
        return true
    }
}