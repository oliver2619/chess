package pluto.chess.notation

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.figure.FigureType
import pluto.chess.move.Move
import java.lang.Math.abs

object AlgebraicNotation : MoveNotation {

    override fun moveToString(move: Move, board: Board): String {
        val ret = StringBuilder()
        if (move.figureType == FigureType.KING && abs(move.targetField.value - move.startField.value) == 2) {
            ret.append(if (move.targetField.value > move.startField.value) "O-O" else "O-O-O")
        } else {
            if (move.figureType != FigureType.PAWN) {
                ret.append(figureToString(move.figureType))
            }
            appendMoveStartField(move, board, ret)
            if (move.isCapturing(board)) {
                ret.append('x')
            }
            ret.append(fieldToString(move.targetField))
            if (move.conversion != null) {
                ret.append('=').append(figureToString(move.conversion.figure))
            }
        }
        val newBoard = board.moved(move)
        if (newBoard.isChecked()) {
            if (newBoard.hasAnyMoves()) {
                ret.append('+')
            } else {
                ret.append('#')
            }
        }
        return ret.toString()
    }

    private fun figureToString(figureType: FigureType): String = when (figureType) {
        FigureType.PAWN -> ""
        FigureType.KNIGHT -> "N"
        FigureType.BISHOP -> "B"
        FigureType.ROOK -> "R"
        FigureType.QUEEN -> "Q"
        FigureType.KING -> "K"
    }

    private fun fieldToString(field: Field): String {
        return "${('a' + field.line)}${'1' + field.row}"
    }

    private fun appendMoveStartField(move: Move, board: Board, builder: StringBuilder) {
        val possibleMoves = mutableListOf<Move>()
        board.findMovesTo(move.figureType, move.targetField, move.conversion) { possibleMoves.add(it.move) }
        if (possibleMoves.size == 0) {
            throw ChessException("Move $move on board not found")
        }
        if (possibleMoves.size == 1) {
            return;
        }
        if (possibleMoves.count { it.startField.line == move.startField.line } == 1) {
            builder.append('a' + move.startField.line)
            return
        }
        if (possibleMoves.count { it.startField.row == move.startField.row } == 1) {
            builder.append('1' + move.startField.row)
            return
        }
        builder.append(fieldToString(move.startField))
    }
}

fun Move.toSan(board: Board): String = AlgebraicNotation.moveToString(this, board)
