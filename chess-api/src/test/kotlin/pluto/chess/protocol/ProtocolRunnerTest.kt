package pluto.chess.protocol

import org.junit.jupiter.api.Test
import pluto.chess.TestEngine
import pluto.chess.engine.OutputStreamEngineLogger
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import kotlin.test.assertContains
import kotlin.test.assertEquals

internal class ProtocolRunnerTest {

    @Test
    internal fun testUciQuit() {
        val input = StringReader("uci\nquit\n")
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        val engine = TestEngine()
        ProtocolRunner { engine }.run(input, output, logger)
        val errorOutputString = errorOutput.toString(Charset.defaultCharset())
        assertEquals("init\ndeinit\n", engine.history)
        assertEquals("", errorOutputString)
    }

    @Test
    internal fun testReturnFromEof() {
        val input = StringReader("")
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        ProtocolRunner { TestEngine() }.run(input, output, logger)
    }

    @Test
    internal fun testUnknownProtocolFirst() {
        val input = StringReader("other\nuci\nquit\n")
        val output = StringWriter()
        val errorOutput = ByteArrayOutputStream()
        val logger = OutputStreamEngineLogger(errorOutput)
        val engine = TestEngine()
        ProtocolRunner { engine }.run(input, output, logger)
        val errorOutputString = errorOutput.toString(Charset.defaultCharset())
        assertContains(errorOutputString, "ChessException")
        assertContains(errorOutputString, "other")
        assertEquals("init\ndeinit\n", engine.history)
    }
}
