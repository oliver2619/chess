package pluto.chess.protocol.uci

import pluto.chess.board.Board
import pluto.chess.move.MoveWithBoard

internal interface UciProtocolInterface {

    fun go(searchOptions: UciSearchOptions)

    fun newGame()

    fun ponderHit()

    fun setDebug(debug: Boolean)

    fun setOption(name: String, value: String?)

    fun setPosition(startPosition: Board, moves: List<MoveWithBoard>)

    fun stop()

    fun quit()

    fun waitForIdle()
}