package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.*

internal class MiddlePawn(color: FigureColor, field: Field) : Pawn(color, field) {

    private val capturingMoves: Array<Move> = createPawnCapturingMoves(color, field)
    private val lineMove: Move = PawnNonCapturingMove(color, field)

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? {
        if (conversion != null) {
            return null
        }
        for (move in capturingMoves) {
            if (move.targetField == targetField && move.isNotBlocked(board)) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    return MoveWithBoard(move, newBoard)
                }
            }
        }
        if (lineMove.targetField == targetField && lineMove.isNotBlocked(board)) {
            val newBoard = board.moved(lineMove)
            if (!newBoard.isKingAttacked(color)) {
                return MoveWithBoard(lineMove, newBoard)
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
        if (ret == MoveConsumerResult.CONTINUE_ALL && lineMove.isNotBlocked(board)) {
            val newBoard = board.moved(lineMove)
            if (!newBoard.isKingAttacked(color)) {
                ret = ret combine consumer.move(lineMove, newBoard)
            }
        }
        return ret
    }

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean {
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (move in capturingMoves) {
            if (move.isNotBlocked(board)) {
                val newBoard = board.moved(move)
                if (!newBoard.isKingAttacked(color)) {
                    ret = ret combine consumer.move(move, newBoard)
                    if (ret == MoveConsumerResult.CANCEL) {
                        return false
                    }
                }
            }
        }
        return true
    }

    companion object {

        private fun createPawnCapturingMoves(color: FigureColor, startField: Field): Array<Move> {
            val ret = mutableListOf<Move>()
            Masks.pawnAttack(startField, color)
                .streamFields()
                .forEach { targetField ->
                    ret.add(PawnCapturingMove(color, startField, targetField))
                    if ((color == FigureColor.WHITE && startField.row == 4) || (color == FigureColor.BLACK && startField.row == 3)) {
                        ret.add(PawnEnPassantMove(color, startField, targetField))
                    }
                }
            return ret.toTypedArray()
        }
    }
}