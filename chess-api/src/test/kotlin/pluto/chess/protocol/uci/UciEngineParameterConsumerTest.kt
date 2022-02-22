package pluto.chess.protocol.uci

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pluto.chess.ChessException
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class UciEngineParameterConsumerTest {

    private lateinit var responseWriter: StringWriter
    private lateinit var parameterValueMapper: UciEngineParameterValueMapper
    private lateinit var it: UciEngineParameterConsumer

    private val response: String get() = responseWriter.toString()

    @BeforeEach
    internal fun init() {
        responseWriter = StringWriter()
        parameterValueMapper = UciEngineParameterValueMapper()
        it = UciEngineParameterConsumer(UciOptionsResponseBuilder(responseWriter), parameterValueMapper)
    }

    @Test
    internal fun testBoolean() {
        it.booleanParameter("bool", true)
        assertEquals("option name bool type check default true${System.lineSeparator()}", response)
        assertEquals(true, parameterValueMapper.map("bool", "true"))
    }

    @Test
    internal fun testChoice() {
        it.choiceParameter("list", listOf("one", "two"), "one")
        assertEquals("option name list type combo var one var two default one${System.lineSeparator()}", response)
        assertEquals("one", parameterValueMapper.map("list", "one"))
    }

    @Test
    internal fun testNumber1() {
        it.numberParameter("num", 1, 2, 1)
        assertEquals("option name num type spin min 1 max 2 default 1${System.lineSeparator()}", response)
        assertEquals(1, parameterValueMapper.map("num", "1"))
    }

    @Test
    internal fun testNumber2() {
        it.numberParameter("num", 1, null, 1)
        assertEquals("option name num type spin min 1 default 1${System.lineSeparator()}", response)
    }

    @Test
    internal fun testNumber3() {
        it.numberParameter("num", null, 2, 1)
        assertEquals("option name num type spin max 2 default 1${System.lineSeparator()}", response)
    }

    @Test
    internal fun testNumber4() {
        it.numberParameter("num", null, null, 1)
        assertEquals("option name num type spin default 1${System.lineSeparator()}", response)
    }

    @Test
    internal fun testString() {
        it.stringParameter("str", "def")
        assertEquals("option name str type string default def${System.lineSeparator()}", response)
        assertEquals("val", parameterValueMapper.map("str", "val"))
    }

    @Test
    internal fun testFailures() {
        assertFailsWith<ChessException> { it.stringParameter("UCI_Param", "def") }
        assertFailsWith<ChessException> { it.booleanParameter("Ponder", false) }
        assertFailsWith<ChessException> {
            it.booleanParameter("One", false)
            it.stringParameter("One", "false")
        }
    }
}