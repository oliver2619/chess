package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.board.Board
import pluto.chess.move.MoveWithBoard
import pluto.chess.notation.toFen

private class TestUciProtocolInterface : UciProtocolInterface {

    var record: String = ""
    var board: Board? = null
    var moves: List<MoveWithBoard> = listOf()
    var searchOptions: UciSearchOptions? = null

    override fun go(searchOptions: UciSearchOptions) {
        record += "go()"
        this.searchOptions = searchOptions
    }

    override fun newGame() {
        record += "newGame()"
    }

    override fun ponderHit() {
        record += "ponderHit()"
    }

    override fun setDebug(debug: Boolean) {
        record += "debug=$debug;"
    }

    override fun setOption(name: String, value: String?) {
        this.record += "setOption($name,${value ?: "null"})"
    }

    override fun setPosition(startPosition: Board, moves: List<MoveWithBoard>) {
        board = startPosition
        this.moves = moves
        record += "setPosition()"
    }

    override fun stop() {
        record += "stop()"
    }

    override fun quit() {
        record += "quit()"
    }

    override fun waitForIdle() {
        record += "waitForIdle()"
    }
}

internal class UciCommandsTest {

    @Test
    internal fun testSimpleDebug() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("debug", "false", testInterface)
        UciCommands.execute("debug", "true", testInterface)
        assertEquals("debug=false;debug=true;", testInterface.record)
    }

    @Test
    internal fun testSimpleCommands() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("isready", null, testInterface)
        UciCommands.execute("register", null, testInterface)
        UciCommands.execute("ucinewgame", null, testInterface)
        UciCommands.execute("stop", null, testInterface)
        UciCommands.execute("ponderhit", null, testInterface)
        UciCommands.execute("quit", null, testInterface)
        assertEquals("waitForIdle()newGame()stop()ponderHit()quit()", testInterface.record)
    }

    @Test
    internal fun testSetOption() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("setoption", "name a value true", testInterface)
        UciCommands.execute("setoption", "name b", testInterface)
        assertEquals("setOption(a,true)setOption(b,null)", testInterface.record)
    }

    @Test
    internal fun testSetPosition_startPos() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("position", "startpos", testInterface)
        assertEquals("setPosition()", testInterface.record)
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", testInterface.board?.toFen(0))
        assertEquals(0, testInterface.moves.size)
    }

    @Test
    internal fun testSetPosition_startPosAndMoves() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("position", "startpos moves e2e4 e7e5", testInterface)
        assertEquals("setPosition()", testInterface.record)
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", testInterface.board?.toFen(0))
        assertEquals(2, testInterface.moves.size)
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2", testInterface.moves[1].board.toFen(2))
    }

    @Test
    internal fun testSetPosition_fen() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("position", "fen rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2", testInterface)
        assertEquals("setPosition()", testInterface.record)
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2", testInterface.board?.toFen(2))
        assertEquals(0, testInterface.moves.size)
    }

    @Test
    internal fun testSetPosition_fenAndMoves() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("position", "fen rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2 moves g1f3", testInterface)
        assertEquals("setPosition()", testInterface.record)
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2", testInterface.board?.toFen(2))
        assertEquals(1, testInterface.moves.size)
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2", testInterface.moves[0].board.toFen(3))
    }

    @Test
    internal fun testGo() {
        val testInterface = TestUciProtocolInterface()
        UciCommands.execute("go", "wtime 100 btime 200 winc 5000 binc 2000 movestogo 5", testInterface)
        assertEquals("go()", testInterface.record)
        val searchOptions = testInterface.searchOptions?.getForBoard(Board.newGame())
        assertEquals(100L, searchOptions?.totalTimeMs)
        assertEquals(5000L, searchOptions?.timeMsIncreasePerMove)
        assertEquals(5, searchOptions?.movesUntilNextTimeControl)
    }
}