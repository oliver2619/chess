package pluto.chess.protocol

import org.junit.jupiter.api.Test
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LineBasedChessProtocolTest {

    @Test
    internal fun testCommands() {
        val input = StringReader("one\rtwo\nthree\r\nfour\n\r")
        val output = mutableListOf<String>()
        assertTrue(LineBasedChessProtocol.processInput(input){output.add(it)})
        assertTrue(LineBasedChessProtocol.processInput(input){output.add(it)})
        assertTrue(LineBasedChessProtocol.processInput(input){output.add(it)})
        assertTrue(LineBasedChessProtocol.processInput(input){output.add(it)})
        assertFalse(LineBasedChessProtocol.processInput(input){output.add(it)})
        assertEquals(4, output.size)
        assertEquals("one", output[0])
        assertEquals("two", output[1])
        assertEquals("three", output[2])
        assertEquals("four", output[3])
    }
}