package pluto.chess.protocol.uci

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.StringWriter

internal class UciOptionsResponseBuilderTest {

    @Test
    internal fun testButton() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).button("btn")
        assertEquals("option name btn type button${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testBool() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).boolean("bool")
        assertEquals("option name bool type check${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testBool_defaultFalse() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).boolean("bool", false)
        assertEquals("option name bool type check default false${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testBool_defaultTrue() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).boolean("bool", true)
        assertEquals("option name bool type check default true${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testChoice() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).choice("list", listOf("one", "two"))
        assertEquals("option name list type combo var one var two${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testChoice_default() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).choice("list", listOf("one", "two"), "one")
        assertEquals("option name list type combo var one var two default one${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testNumber() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).number("num", null, null, null)
        assertEquals("option name num type spin${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testNumber_vals() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).number("num", 1, 10, 5)
        assertEquals("option name num type spin min 1 max 10 default 5${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testString() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).string("str")
        assertEquals("option name str type string${System.lineSeparator()}", writer.toString())
    }

    @Test
    internal fun testString_def() {
        val writer = StringWriter()
        UciOptionsResponseBuilder(writer).string("str", "def")
        assertEquals("option name str type string default def${System.lineSeparator()}", writer.toString())
    }

}