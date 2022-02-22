package pluto.chess

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

internal class ChessExceptionTest {

    @Test
    internal fun test1() {
        val reason = NullPointerException()
        assertEquals("msg", ChessException("msg").message)
        assertEquals("msg", ChessException("msg", reason).message)
        assertSame(reason, ChessException("msg", reason).cause)
    }
}