package pluto.chess.notation.pgn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.ChessException
import pluto.chess.board.GameResult
import java.time.LocalDate
import kotlin.test.assertFailsWith

internal class PgnMetaDataTest {

    @Test
    internal fun test() {
        val metaData = PgnMetaDataBuilder().event("eventA")
            .site("city", "region", "country")
            .round(24)
            .white("White", "Mrs.")
            .black("Black", "Mr.")
            .date(LocalDate.of(2022,6, 15))
            .result(GameResult.WHITE_WON)
            .data("Custom", "customValue")
            .build()

        assertEquals("eventA", metaData.event)
        assertEquals("city, region, country", metaData.site)
        assertEquals(24, metaData.round)
        assertEquals("24", metaData.roundString)
        assertEquals("White, Mrs.", metaData.white)
        assertEquals("Black, Mr.", metaData.black)
        assertEquals("2022.06.15", metaData.dateString)
        assertEquals(LocalDate.of(2022, 6, 15), metaData.date)
        assertEquals(GameResult.WHITE_WON, metaData.result)
        assertEquals("customValue", metaData.getData("Custom"))
        assertEquals(1, metaData.getDataKeys().size)
        assertTrue(metaData.getDataKeys().contains("Custom"))
        assertEquals(1, metaData.getDataEntries().size)
        assertTrue(metaData.getDataEntries().map{e -> e.key}.contains("Custom"))
        assertTrue(metaData.getDataEntries().map{e -> e.value}.contains("customValue"))
    }

    @Test
    internal fun test_illegalValues() {
        assertFailsWith<ChessException> { PgnMetaDataBuilder().data("FEN", "wrong") }
        assertFailsWith<ChessException> { PgnMetaDataBuilder().data("SetUp", "wrong") }
        assertFailsWith<ChessException> { PgnMetaDataBuilder().build() }
    }
}