package pluto.chess.notation.pgn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.board.Board
import pluto.chess.board.GameResult
import pluto.chess.notation.toFen

internal class PgnParserTest {

    @Test
    internal fun testParse_metadata() {
        val handler = SinglePgnParserHandler()
        this.javaClass.getResourceAsStream("/test.pgn")?.use { PgnParser.parse(it, handler) }
        assertNotNull(handler.metaData)
        handler.metaData?.let {
            assertEquals("Wch1", it.event)
            assertEquals("U.S.A.", it.site)
            assertEquals("1886.??.??", it.dateString)
            assertEquals("9", it.roundString)
            assertEquals("Zukertort, Johannes", it.white)
            assertEquals("Steinitz, Wilhelm", it.black)
            assertEquals(GameResult.BLACK_WON, it.result)
            assertEquals("D26h", it.getData("ECO"))
            assertEquals("JvR", it.getData("Annotator"))
            assertEquals(2, it.getDataKeys().size)
        }
        assertEquals(GameResult.BLACK_WON, handler.result)
    }

    @Test
    internal fun testParse_remis() {
        val handler = SinglePgnParserHandler()
        this.javaClass.getResourceAsStream("/testRemis.pgn")?.use { PgnParser.parse(it, handler) }
        assertEquals(GameResult.REMIS, handler.metaData?.result)
        assertEquals(GameResult.REMIS, handler.result)
    }

    @Test
    internal fun testParse_another() {
        this.javaClass.getResourceAsStream("/test3.pgn")?.use { PgnParser.parse(it, DefaultPgnParserHandler()) }
    }

    @Test
    internal fun testParseConversion() {
        this.javaClass.getResourceAsStream("/testConversion.pgn")
            ?.use { PgnParser.parse(it, DefaultPgnParserHandler()) }
    }

    @Test
    internal fun testParseFen() {
        val handler = SinglePgnParserHandler()
        this.javaClass.getResourceAsStream("/testFen.pgn")?.use { PgnParser.parse(it, handler) }
        assertEquals("r1bqkbnr/pp1ppppp/2n5/2p5/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 3 3", handler.board?.toFen(5))
    }

    @Test
    internal fun testMultipleGames_all() {
        var totalGames = 0
        this.javaClass.getResourceAsStream("/FideChamp1993.pgn")?.use {
            PgnParser.parse(it, object : DefaultPgnParserHandler() {
                override fun end(board: Board, result: GameResult) {
                    ++totalGames
                }
            })
        }
        assertEquals(21, totalGames)
    }

    @Test
    internal fun testMultipleGames_singleTake() {
        var totalGames = 0
        val handler = object : SinglePgnParserHandler() {
            override fun onEnd(board: Board, result: GameResult) {
                ++totalGames
            }
        }
        this.javaClass.getResourceAsStream("/FideChamp1993.pgn")?.use { PgnParser.parse(it, handler) }
        assertEquals(1, totalGames)
        assertEquals("1", handler.metaData?.roundString)
    }
}