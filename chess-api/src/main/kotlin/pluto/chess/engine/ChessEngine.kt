package pluto.chess.engine

import pluto.chess.board.Board

interface ChessEngine {

    val metaData: ChessEngineMetaData

    fun executeAction(name: String)

    fun init(logger: EngineLogger)

    fun deinit()

    fun setOpponent(name: String, human: Boolean)

    fun setParameter(name: String, value: Any)

    fun useOwnOpeningBook(use: Boolean)

    fun newGame()

    fun searchOnTurn(board: Board, options: EngineSearchOptions, finishedCallback: () -> Unit)

    fun searchPassively(board: Board)

    fun getBestThinkLine(): EngineThinkLine

    fun stop()
}