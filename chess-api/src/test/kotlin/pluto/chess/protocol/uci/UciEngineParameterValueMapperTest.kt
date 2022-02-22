package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.ChessException
import kotlin.test.assertFailsWith

internal class UciEngineParameterValueMapperTest {

    @Test
    internal fun test() {
        val mapper = UciEngineParameterValueMapper()
        mapper.registerBoolean("bool")
        mapper.registerChoice("list", listOf("one", "two"))
        mapper.registerNumber("num12", 1, 2)
        mapper.registerNumber("num1", 1, null)
        mapper.registerNumber("num2", null, 2)
        mapper.registerNumber("num", null, null)
        mapper.registerString("str")
        assertEquals(true, mapper.map("bool", "true"))
        assertEquals(false, mapper.map("bool", "false"))
        assertEquals("one", mapper.map("list", "one"))
        assertEquals("two", mapper.map("list", "two"))
        assertFailsWith<ChessException> { mapper.map("list", "other") }
        assertEquals(1, mapper.map("num12", "1"))
        assertEquals(10, mapper.map("num1", "10"))
        assertEquals(-1, mapper.map("num2", "-1"))
        assertEquals(5, mapper.map("num", "5"))
        assertFailsWith<ChessException> { mapper.map("num12", "-1") }
        assertFailsWith<ChessException> { mapper.map("num12", "10") }
        assertFailsWith<ChessException> { mapper.map("num1", "-1") }
        assertFailsWith<ChessException> { mapper.map("num2", "10") }
        assertFailsWith<ChessException> { mapper.map("other", "0") }
        assertFailsWith<ChessException> { mapper.registerButton("bool") }
    }
}