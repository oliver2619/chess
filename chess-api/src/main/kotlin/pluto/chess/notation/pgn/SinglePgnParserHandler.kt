package pluto.chess.notation.pgn

import pluto.chess.board.Board
import pluto.chess.board.GameResult
import pluto.chess.move.Move

open class SinglePgnParserHandler : PgnParserHandler {

    private var _metaData: PgnMetaData? = null
    private var _board: Board? = null

    val metaData: PgnMetaData? get() = _metaData

    val board: Board? get() = _board

    val result: GameResult? get() = _metaData?.result

    final override fun addMove(move: Move, index: Int, previousBoard: Board, currentBoard: Board): Boolean {
        onAddMove(move, index, previousBoard, currentBoard)
        return true
    }

    protected open fun onAddMove(move: Move, index: Int, previousBoard: Board, currentBoard: Board) {}

    final override fun end(board: Board, result: GameResult) {
        this._board = board
        onEnd(board, result)
    }

    protected open fun onEnd(board: Board, result: GameResult) {}

    override fun setInitialBoard(board: Board) {}

    final override fun setMetaData(metaData: PgnMetaData): PgnParserHandlerResult {
        if (_metaData == null) {
            _metaData = metaData
            return PgnParserHandlerResult.CONTINUE
        } else {
            return PgnParserHandlerResult.CANCEL
        }
    }

    override fun setProgress(read: Int, total: Int) {}
}