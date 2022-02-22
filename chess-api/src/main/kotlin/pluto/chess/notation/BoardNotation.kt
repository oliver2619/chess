package pluto.chess.notation

import pluto.chess.board.Board

interface BoardNotation {

    fun boardToString(board: Board, totalMoves: Int): String

    fun parseBoard(board: String): Board
}