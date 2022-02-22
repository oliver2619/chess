package pluto.chess

import pluto.chess.board.Board
import pluto.chess.engine.*
import pluto.chess.move.MoveWithBoard
import pluto.chess.notation.toFen

class TestEngine : ChessEngine {

    var history = ""

    private var board: Board = Board.newGame()

    override val metaData: ChessEngineMetaData
        get() {
            return object : ChessEngineMetaData {

                override val name: String = "testEngine"
                override val author: String = "testAuthor"
                override val hasOwnOpeningBook: Boolean = false
                override val supportsPermanentBrain: Boolean = false

                override fun listParameters(consumer: EngineParameterConsumer) {
                    consumer.stringParameter("key", "defaultValue")
                }
            }
        }

    override fun executeAction(name: String) {
        history += "action $name\n"
    }

    override fun init(logger: EngineLogger) {
        history += "init\n"
    }

    override fun deinit() {
        history += "deinit\n"
    }

    override fun setOpponent(name: String, human: Boolean) {
        history += "setOpponent $name ${if (human) "human" else "ai"}\n"
    }

    override fun setParameter(name: String, value: Any) {
        history += "setParameter $name $value\n"
    }

    override fun useOwnOpeningBook(use: Boolean) {
        history += "useOwnOpeningBook $use\n"
    }

    override fun newGame() {
        history += "newGame\n"
    }

    override fun searchOnTurn(board: Board, options: EngineSearchOptions, finishedCallback: () -> Unit) {
        history += "searchOnTurn ${board.toFen(0)}\n"
        this.board = board
        finishedCallback()
    }

    override fun searchPassively(board: Board) {
        history += "searchPassively ${board.toFen(0)}\n"
        this.board = board
    }

    override fun getBestThinkLine(): EngineThinkLine {
        history += "getBestThinkLine\n"
        val ret: List<MoveWithBoard> = listOf(board.findMoves().get(0))
        return EngineThinkLine(ret, 0)
    }

    override fun stop() {
        history += "stop\n"
    }
}