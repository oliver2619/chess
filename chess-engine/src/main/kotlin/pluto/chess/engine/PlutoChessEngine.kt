package pluto.chess.engine

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import pluto.chess.board.Board
import pluto.chess.engine.ai.BoardRater
import pluto.chess.engine.ai.SimpleMaterialRater
import pluto.chess.engine.ai.ThinkingTree
import pluto.chess.protocol.ProtocolRunner
import kotlin.concurrent.thread

class PlutoChessEngine(rater: BoardRater) : ChessEngine {

    private val tree = ThinkingTree(rater)
    private var thinkingThread: Thread? = null

    override val metaData: ChessEngineMetaData = object : ChessEngineMetaData {

        override val name = "Pluto"

        override val author = "Oliver"

        override val hasOwnOpeningBook = false

        override val supportsPermanentBrain = false

        override fun listParameters(consumer: EngineParameterConsumer) {
        }
    }

    override fun executeAction(name: String) {}

    override fun init(logger: EngineLogger) {}

    override fun deinit() {
        stopThread()
    }

    override fun setOpponent(name: String, human: Boolean) {}

    override fun setParameter(name: String, value: Any) {}

    override fun useOwnOpeningBook(use: Boolean) {}

    override fun newGame() {
        stopThread()
        tree.reset()
    }

    override fun searchOnTurn(board: Board, options: EngineSearchOptions, finishedCallback: () -> Unit) {

        thinkingThread = thread(priority = 1) {}

    }

    override fun searchPassively(board: Board) {}

    override fun getBestThinkLine(): EngineThinkLine {
        TODO("Not yet implemented")
    }

    override fun stop() {
        stopThread()
    }

    private fun stopThread() {
        val dings: Deferred<Boolean>
        runBlocking {
            dings = async {
                println("Hello")
                true
            }
            dings.await()
        }
        thinkingThread?.join().also { thinkingThread = null }
    }
}

fun main(args: Array<String>) {

    ProtocolRunner { PlutoChessEngine(SimpleMaterialRater()) }.run(
        System.`in`,
        System.out,
        OutputStreamEngineLogger(System.err)
    )

}

