package pluto.chess.protocol.uci

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.move.Conversion
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

internal object UciNotation {

    fun parse(move: String, board: Board): MoveWithBoard {
        val startLine = (move[0] - 'a').toInt()
        val startRow = (move[1] - '1').toInt()
        val targetLine = (move[2] - 'a').toInt()
        val targetRow = (move[3] - '1').toInt()
        val conversion = if (move.length == 5) {
            when (move[4]) {
                'n' -> Conversion.KNIGHT
                'b' -> Conversion.BISHOP
                'r' -> Conversion.ROOK
                'q' -> Conversion.QUEEN
                else -> throw ChessException("Illegal conversion for move '$move'")
            }
        } else {
            null
        }
        return board.findMove(Field(startLine + startRow * 8), Field(targetLine + targetRow * 8), conversion)
            ?: throw ChessException("Move '$move' is not possible")
    }

    fun toString(move: Move): String {
        val conversion = if (move.conversion != null) {
            when (move.conversion) {
                Conversion.KNIGHT -> 'n'
                Conversion.BISHOP -> 'b'
                Conversion.ROOK -> 'r'
                Conversion.QUEEN -> 'q'
            }
        } else {
            ""
        }
        val startField = "${'a' + move.startField.line}${'1' + move.startField.row}"
        val targetField = "${'a' + move.targetField.line}${'1' + move.targetField.row}"
        return "$startField$targetField$conversion"
    }
}