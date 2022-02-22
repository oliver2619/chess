package pluto.chess.notation.pgn

import pluto.chess.ChessException
import pluto.chess.board.GameResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PgnMetaData(
    val event: String,
    val site: String,
    val white: String,
    val black: String,
    val result: GameResult,
    val dateString: String,
    val roundString: String,
    private val data: Map<String, String>
) {

    val date: LocalDate
        get() = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT))

    val round: Int
        get() = roundString.toInt()

    fun getData(name: String): String? = data[name]

    fun getDataKeys(): Set<String> = data.keys

    fun getDataEntries() = data.entries

    companion object {
        internal const val DATE_FORMAT = "yyyy.MM.dd"
    }
}