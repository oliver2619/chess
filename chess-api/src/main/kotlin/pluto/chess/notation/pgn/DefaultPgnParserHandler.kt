package pluto.chess.notation.pgn

import pluto.chess.board.Board
import pluto.chess.board.GameResult
import pluto.chess.move.Move

open class DefaultPgnParserHandler : PgnParserHandler {

    override fun addMove(move: Move, index: Int, previousBoard: Board, currentBoard: Board) = true

    override fun end(board: Board, result: GameResult) {}

    override fun setInitialBoard(board: Board) {}

    override fun setMetaData(metaData: PgnMetaData) = PgnParserHandlerResult.CONTINUE

    override fun setProgress(read: Int, total: Int) {}
}