package pluto.chess.notation.pgn

import pluto.chess.board.Board
import pluto.chess.board.GameResult
import pluto.chess.move.Move

interface PgnParserHandler {

    fun addMove(move: Move, index: Int, previousBoard: Board, currentBoard: Board): Boolean

    fun end(board: Board, result: GameResult)

    fun setInitialBoard(board: Board)

    fun setMetaData(metaData: PgnMetaData): PgnParserHandlerResult

    fun setProgress(read: Int, total: Int)
}