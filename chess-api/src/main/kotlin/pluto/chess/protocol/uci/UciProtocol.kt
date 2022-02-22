package pluto.chess.protocol.uci

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.engine.ChessEngine
import pluto.chess.engine.EngineLogger
import pluto.chess.engine.EngineSearchOptions
import pluto.chess.move.MoveWithBoard
import pluto.chess.protocol.LineBasedChessProtocol
import java.io.Reader
import java.io.Writer
import java.util.regex.Pattern

private enum class UciProtocolState {
    IDLE, SEARCH, PONDER, EXIT
}

internal class UciProtocol(
    private val reader: Reader,
    writer: Writer,
    private val engine: ChessEngine,
    private val logger: EngineLogger
) : Runnable {
    private val responseWriter = UciResponseWriter(writer)
    private val valueMapper = UciEngineParameterValueMapper()
    private val uciProtocolInterface = object : UciProtocolInterface {

        override fun go(searchOptions: UciSearchOptions) = this@UciProtocol.go(searchOptions)

        override fun newGame() = this@UciProtocol.newGame()

        override fun ponderHit() = this@UciProtocol.ponderHit()

        override fun setDebug(debug: Boolean) {
            logger.debugEnabled = debug
        }

        override fun setOption(name: String, value: String?) = this@UciProtocol.setOption(name, value)

        override fun setPosition(startPosition: Board, moves: List<MoveWithBoard>) =
            this@UciProtocol.setPosition(startPosition, moves)

        override fun stop() = this@UciProtocol.stop()

        override fun quit() = this@UciProtocol.quit()

        override fun waitForIdle() = responseWriter.uciok()
    }

    private var state = UciProtocolState.IDLE
    private var startPosition = Board.newGame()
    private var history: List<MoveWithBoard> = listOf()
    private var ponderOptions: UciSearchOptions? = null

    override fun run() {
        helo()
        while (state != UciProtocolState.EXIT) {
            try {
                if (!LineBasedChessProtocol.processInput(reader) { executeCommand(it) }) {
                    quit()
                }
            } catch (e: Throwable) {
                logger.logError(e)
            }
        }
    }

    private fun executeCommand(command: String) {
        val matcher = CMD_PATTERN.matcher(command)
        if (matcher.matches()) {
            UciCommands.execute(matcher.group("cmd"), matcher.group("params"), uciProtocolInterface)
        } else {
            throw ChessException("Unrecognised command '$command'")
        }
    }

    private fun go(uciOptions: UciSearchOptions) {
        if (uciOptions.ponder) {
            val board = if (history.size > 1) history[history.size - 2].board else startPosition
            ponderOptions = uciOptions
            state = UciProtocolState.PONDER
            engine.searchPassively(board)
        } else {
            val board = if (history.isNotEmpty()) history[history.size - 1].board else startPosition
            val options = uciOptions.getForBoard(board)
            searchOnTurn(board, options)
        }
    }

    private fun helo() {
        responseWriter.id(engine.metaData.name, engine.metaData.author)
        responseWriter.options.string(UCI_OPPONENT)
        if (engine.metaData.supportsPermanentBrain) {
            responseWriter.options.boolean("Ponder")
        }
        if (engine.metaData.hasOwnOpeningBook) {
            responseWriter.options.boolean(OWN_BOOK)
        }
        engine.metaData.listParameters(UciEngineParameterConsumer(responseWriter.options, valueMapper))
        responseWriter.uciok()
    }

    private fun newGame() {
        startPosition = Board.newGame()
        history = listOf()
        engine.newGame()
    }

    private fun ponderHit() {
        val board = history[history.size - 1].board
        val options = ponderOptions?.getForBoard(board) ?: throw ChessException("No search options available to use")
        searchOnTurn(board, options)
    }

    private fun searchOnTurn(board: Board, options: EngineSearchOptions) {
        state = UciProtocolState.SEARCH
        engine.searchOnTurn(board, options) {
            state = UciProtocolState.IDLE
            writeBestMove()
        }
    }

    private fun setOption(name: String, value: String?) {
        when (name) {
            UCI_OPPONENT -> if (value != null) setOpponent(value)
            OWN_BOOK -> engine.useOwnOpeningBook(value == "true")
            else -> {
                if (value != null) {
                    engine.setParameter(name, valueMapper.map(name, value))
                } else {
                    engine.executeAction(name)
                }
            }
        }
    }

    private fun setOpponent(opponent: String) {
        val matcher = OPPONENT_MATCHER.matcher(opponent)
        if (matcher.matches()) {
            engine.setOpponent(matcher.group("name"), matcher.group("human") == "human")
        }
    }

    private fun setPosition(startPosition: Board, moves: List<MoveWithBoard>) {
        // TODO accelerate. Old position string can be used as a hint to not parse the entire string
        this.startPosition = startPosition
        this.history = moves
    }

    private fun stop() {
        when (state) {
            UciProtocolState.SEARCH, UciProtocolState.PONDER -> engine.stop()
            else -> {}
        }
        if (state == UciProtocolState.SEARCH) {
            writeBestMove()
        }
        state = UciProtocolState.IDLE
    }

    private fun quit() {
        when (state) {
            UciProtocolState.SEARCH, UciProtocolState.PONDER -> engine.stop()
            else -> {}
        }
        state = UciProtocolState.EXIT
    }

    private fun writeBestMove() {
        val line = engine.getBestThinkLine()
        val ponder = if (line.moves.size > 1) UciNotation.toString(line.moves[1].move) else null
        responseWriter.bestMove(UciNotation.toString(line.moves[0].move), ponder)
    }

    private companion object {

        private val CMD_PATTERN = Pattern.compile("^(?<cmd>[^\\s]+)(?:\\s(?<params>.*))?\$")
        private val OPPONENT_MATCHER = Pattern.compile("^[^\\s]+\\s+[^\\s]+\\s+(?<human>computer|human)\\s+(?<name>.*)")
        private val UCI_OPPONENT = "UCI_Opponent"
        private val OWN_BOOK = "OwnBook"
    }
}