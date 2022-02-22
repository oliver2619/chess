package pluto.chess.protocol.uci

import org.junit.jupiter.api.Test
import pluto.chess.TestEngine
import pluto.chess.board.Board
import pluto.chess.board.BoardEditor
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.engine.OutputStreamEngineLogger
import pluto.chess.figure.FigureColor
import pluto.chess.notation.toFen
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class UciProtocolTest {

    @Test
    internal fun test_init_debug_setOption_isready() {
        val input = StringReader(
            arrayOf(
                "debug true",
                "setoption name key value one",
                "setoption name custom",
                "setoption name UCI_Opponent value none none computer aiName",
                "setoption name UCI_Opponent value none none human userName",
                "setoption name OwnBook value true",
                "setoption name OwnBook value false",
                "isready",
                "quit"
            ).joinToString(System.lineSeparator())
        )
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        val engine = TestEngine()
        UciProtocol(input, output, engine, logger).run()
        val outputs = output.toString().trim().split(System.lineSeparator())
        val engineHistory = engine.history.trim().split(System.lineSeparator())
        assertEquals(6, outputs.size)
        assertTrue(outputs.indexOf("id name testEngine") in 0..1)
        assertTrue(outputs.indexOf("id author testAuthor") in 0..1)
        assertTrue(outputs.indexOf("option name UCI_Opponent type string") in 2..3)
        assertTrue(outputs.indexOf("option name key type string default defaultValue") in 2..3)
        assertEquals("uciok", outputs[4])
        assertEquals("uciok", outputs[5])
        assertEquals(6, engineHistory.size)
        assertEquals("setParameter key one", engineHistory[0])
        assertEquals("action custom", engineHistory[1])
        assertEquals("setOpponent aiName ai", engineHistory[2])
        assertEquals("setOpponent userName human", engineHistory[3])
        assertEquals("useOwnOpeningBook true", engineHistory[4])
        assertEquals("useOwnOpeningBook false", engineHistory[5])
    }

    @Test
    internal fun test_init_ucinewgame_position_go_stop_quit() {
        val input = StringReader(
            arrayOf(
                "ucinewgame",
                "position startpos",
                "go infinite",
                "stop",
                "quit"
            ).joinToString(System.lineSeparator())
        )
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        val engine = TestEngine()
        UciProtocol(input, output, engine, logger).run()
        val outputs = output.toString().trim().split(System.lineSeparator())
        val engineHistory = engine.history.trim().split(System.lineSeparator())
        assertEquals(6, outputs.size)
        assertTrue(outputs.indexOf("id name testEngine") in 0..1)
        assertTrue(outputs.indexOf("id author testAuthor") in 0..1)
        assertTrue(outputs.indexOf("option name UCI_Opponent type string") in 2..3)
        assertTrue(outputs.indexOf("option name key type string default defaultValue") in 2..3)
        assertEquals("uciok", outputs[4])
        assertEquals("bestmove ", outputs[5].substring(0, "bestmove ".length))
        assertEquals(3, engineHistory.size)
        assertEquals("newGame", engineHistory[0])
        assertEquals("searchOnTurn ${Board.newGame().toFen(0)}", engineHistory[1])
        assertEquals("getBestThinkLine", engineHistory[2])
    }

    @Test
    internal fun test_init_ucinewgame_position_go_ponder_ponderhit_stop_quit() {
        val input = StringReader(
            arrayOf(
                "ucinewgame",
                "position startpos",
                "go infinite",
                "stop",
                "position startpos moves e2e4 e7e5",
                "go ponder infinite",
                "ponderhit",
                "stop",
                "quit"
            ).joinToString(System.lineSeparator())
        )
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        val engine = TestEngine()
        UciProtocol(input, output, engine, logger).run()
        val outputs = output.toString().trim().split(System.lineSeparator())
        val engineHistory = engine.history.trim().split(System.lineSeparator())
        val board1 = BoardEditor.fromStart().moveFigure(Field.E._2, Field.E._4).resume(FigureColor.BLACK, Flags.CASTLE_ALL or Flags.enPassant(Field.E._2.line))
        val board2 = BoardEditor.fromBoard(board1).moveFigure(Field.E._7, Field.E._5).resume(FigureColor.WHITE, Flags.CASTLE_ALL or Flags.enPassant(Field.E._2.line))
        assertEquals(7, outputs.size)
        assertTrue(outputs.indexOf("id name testEngine") in 0..1)
        assertTrue(outputs.indexOf("id author testAuthor") in 0..1)
        assertTrue(outputs.indexOf("option name UCI_Opponent type string") in 2..3)
        assertTrue(outputs.indexOf("option name key type string default defaultValue") in 2..3)
        assertEquals("uciok", outputs[4])
        assertEquals("bestmove ", outputs[5].substring(0, "bestmove ".length))
        assertEquals("bestmove ", outputs[6].substring(0, "bestmove ".length))
        assertEquals(6, engineHistory.size)
        assertEquals("newGame", engineHistory[0])
        assertEquals("searchOnTurn ${Board.newGame().toFen(0)}", engineHistory[1])
        assertEquals("getBestThinkLine", engineHistory[2])
        assertEquals("searchPassively ${board1.toFen(0)}", engineHistory[3])
        assertEquals("searchOnTurn ${board2.toFen(0)}", engineHistory[4])
        assertEquals("getBestThinkLine", engineHistory[5])
    }
}
