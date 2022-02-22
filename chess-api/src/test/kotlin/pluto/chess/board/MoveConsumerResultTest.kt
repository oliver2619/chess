package pluto.chess.board

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

internal class MoveConsumerResultTest{

    @Test
    internal fun testCombine() {
        assertEquals(MoveConsumerResult.CANCEL, MoveConsumerResult.CANCEL combine MoveConsumerResult.CANCEL)
        assertEquals(MoveConsumerResult.CANCEL, MoveConsumerResult.CANCEL combine MoveConsumerResult.CONTINUE_ALL)
        assertEquals(MoveConsumerResult.CANCEL, MoveConsumerResult.CANCEL combine MoveConsumerResult.CONTINUE_VALUE_CHANGING)

        assertEquals(MoveConsumerResult.CANCEL, MoveConsumerResult.CONTINUE_ALL combine MoveConsumerResult.CANCEL)
        assertEquals(MoveConsumerResult.CONTINUE_ALL, MoveConsumerResult.CONTINUE_ALL combine MoveConsumerResult.CONTINUE_ALL)
        assertEquals(MoveConsumerResult.CONTINUE_VALUE_CHANGING, MoveConsumerResult.CONTINUE_ALL combine MoveConsumerResult.CONTINUE_VALUE_CHANGING)

        assertEquals(MoveConsumerResult.CANCEL, MoveConsumerResult.CONTINUE_VALUE_CHANGING combine MoveConsumerResult.CANCEL)
        assertEquals(MoveConsumerResult.CONTINUE_VALUE_CHANGING, MoveConsumerResult.CONTINUE_VALUE_CHANGING combine MoveConsumerResult.CONTINUE_ALL)
        assertEquals(MoveConsumerResult.CONTINUE_VALUE_CHANGING, MoveConsumerResult.CONTINUE_VALUE_CHANGING combine MoveConsumerResult.CONTINUE_VALUE_CHANGING)
    }

}