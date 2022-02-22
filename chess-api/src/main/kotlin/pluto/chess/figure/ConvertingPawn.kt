package pluto.chess.figure

import pluto.chess.board.*
import pluto.chess.move.*

internal class ConvertingPawn(color: FigureColor, field: Field) : Pawn(color, field) {

    private val moveLists: Array<Array<Move>> = createConvertingMoves(color, field)

    override fun findMove(board: Board, targetField: Field, conversion: Conversion?): MoveWithBoard? {
        if (conversion == null) {
            return null
        }
        for (moveList in moveLists) {
            if (moveList[0].targetField == targetField) {
                if (moveList[0].isNotBlocked(board)) {
                    val newBoard = board.moved(moveList[0])
                    if (!newBoard.isKingAttacked(color)) {
                        return MoveWithBoard(moveList[conversion.ordinal], board.moved(moveList[conversion.ordinal]))
                    }
                }
                return null
            }
        }
        return null
    }

    override fun getMoves(board: Board, consumer: MoveConsumer): MoveConsumerResult {
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (moveList in moveLists) {
            if (moveList[0].isNotBlocked(board)) {
                val newBoard = board.moved(moveList[0])
                if (!newBoard.isKingAttacked(color)) {
                    ret = ret combine consumer.move(moveList[0], newBoard)
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                    ret = ret combine consumer.move(moveList[1], board.moved(moveList[1]))
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                    ret = ret combine consumer.move(moveList[2], board.moved(moveList[2]))
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                    ret = ret combine consumer.move(moveList[3], board.moved(moveList[3]))
                    if (ret == MoveConsumerResult.CANCEL) {
                        return ret
                    }
                }
            }
        }
        return ret
    }

    override fun getValueChangingMoves(board: Board, consumer: MoveConsumer): Boolean {
        return getMoves(board, consumer) != MoveConsumerResult.CANCEL
    }

    companion object {

        private fun createConvertingMoves(color: FigureColor, field: Field): Array<Array<Move>> {
            val moves = mutableListOf<Array<Move>>()
            Masks.pawnAttack(field, color).forEachField { targetField ->
                moves.add(
                    Conversion.values().map { conversion -> PawnCapturingMove(color, field, targetField, conversion) }
                        .toTypedArray()
                )
                MoveConsumerResult.CONTINUE_ALL
            }
            moves.add(Conversion.values().map { conversion -> PawnNonCapturingMove(color, field, conversion) }
                .toTypedArray())
            return moves.toTypedArray()
        }
    }
}