package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.*
import pluto.chess.move.DefaultMove
import pluto.chess.move.PawnDoubleStepMove
import pluto.chess.move.PawnNonCapturingMove

internal class StartPawn(color: FigureColor, field: Field) : Pawn(color, field) {

    private val lineMoves: Array<Move> = arrayOf(PawnNonCapturingMove(color, field), PawnDoubleStepMove(color, field))

    private val capturingMoves: Array<Move> = Masks.pawnAttack(field, color)
        .streamFields()
        .map { targetField -> PawnCapturingMove(color, field, targetField) }
        .toArray { arrayOfNulls<Move>(it) }

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? {
        if (conversion != null) {
            return null
        }
        for (move in capturingMoves) {
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
        for (move in lineMoves) {
            if (move.isNotBlocked(board)) {
                if (move.targetField == targetField) {
                    val newBoard = board.moved(move)
                    if (!newBoard.isKingAttacked(color)) {
                        return MoveWithBoard(move, newBoard)
                    }
                    return null
                }
            } else {
                break
            }
        }
        return null
    }

    override fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult {
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (move in capturingMoves) {
            if (move.isNotBlocked(board)) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    ret = ret combine consumer.move(move, newBoard)
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                }
            }
        }
        if (ret != MoveConsumerResult.CONTINUE_ALL) {
            return ret
        }
        for (move in lineMoves) {
            if (move.isNotBlocked(board)) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    ret = ret combine consumer.move(move, newBoard)
                    if (ret != MoveConsumerResult.CONTINUE_ALL) {
                        return ret
                    }
                }
            } else {
                break
            }
        }
        return ret
    }

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean {
        for (move in capturingMoves) {
            if (move.isNotBlocked(board)) {
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