package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.StringWriter

internal class UciResponseWriterTest {

    @Test
    internal fun testInfo() {
        val writer = StringWriter()
        val uw = UciResponseWriter(writer)
        uw.info.scoreCp(5).string("hello").write()
        uw.info.timeMs(100).write()
        assertEquals(
            "info score cp 5 string hello${System.lineSeparator()}info time 100${System.lineSeparator()}",
            writer.toString()
        )
    }

    @Test
    internal fun testOptions() {
        val writer = StringWriter()
        UciResponseWriter(writer).options.string("str")
        assertEquals("option name str type string${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testBestMove() {
        val writer = StringWriter()
        UciResponseWriter(writer).bestMove("e2-e4")
        assertEquals("bestmove e2-e4${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testBestMovePonder() {
        val writer = StringWriter()
        UciResponseWriter(writer).bestMove("e2-e4", "e7-e5")
        assertEquals("bestmove e2-e4 ponder e7-e5${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testId() {
        val writer = StringWriter()
        UciResponseWriter(writer).id("ename", "aname")
        assertEquals(
            "id name ename${System.lineSeparator()}id author aname${System.lineSeparator()}",
            writer.toString()
        )
    }

    @Test
    internal fun testReadyOk() {
        val writer = StringWriter()
        UciResponseWriter(writer).readyOk()
        assertEquals("readyok${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testUciOk() {
        val writer = StringWriter()
        UciResponseWriter(writer).uciok()
        assertEquals("uciok${System.lineSeparator()}", writer.toString())
    }

}