package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.StringWriter

internal class UciInfoResponseBuilderTest {

    @Test
    internal fun test1() {
        val writer = StringWriter()
        UciInfoResponseBuilder(writer)
            .bestLine(listOf("e2-e4", "e7-e5"))
            .currentMove("d2-d4")
            .depthPlies(5)
            .nodes(80)
            .scoreCp(125)
            .string("hello")
            .timeMs(100)
            .write()
        assertEquals(
            "info depth 5 nodes 80 time 100 nps 800 score cp 125 currmove d2-d4 pv e2-e4 e7-e5 string hello${System.lineSeparator()}",
            writer.toString()
        )
    }

    @Test
    internal fun test2() {
        val writer = StringWriter()
        UciInfoResponseBuilder(writer)
            .scoreMate(3)
            .write()
        assertEquals(
            "info score mate 3${System.lineSeparator()}",
            writer.toString()
        )
    }
}