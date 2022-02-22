package pluto.chess.notation.pgn

import pluto.chess.ChessException
import pluto.chess.board.GameResult
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PgnMetaDataBuilder {

    private var event: String = ""
    private var site: String = ""
    private var white: String = ""
    private var black: String = ""
    private var result: GameResult = GameResult.UNDEFINED
    private var date: String = "??"
    private var round: String = "1"
    private val data = LinkedHashMap<String, String>()

    fun event(event: String) = apply { this.event = event }

    fun site(site: String) = apply { this.site = site }
    fun site(city: String, region: String, country: String) = site("$city, $region, $country")

    fun white(white: String) = apply { this.white = white }
    fun white(lastName: String, firstName: String) = white("$lastName, $firstName")

    fun black(black: String) = apply { this.black = black }
    fun black(lastName: String, firstName: String) = black("$lastName, $firstName")

    fun result(result: GameResult) = apply { this.result = result }

    fun date(date: String) = apply { this.date = date }
    fun date(date: LocalDate) = date(date.format(DateTimeFormatter.ofPattern(PgnMetaData.DATE_FORMAT)))

    fun round(round: String) = apply { this.round = round }
    fun round(round: Int) = round(round.toString())

    fun data(name: String, value: String) = apply {
        if (RESERVED_KEYS.contains(name)) {
            throw ChessException("Illegal use of reserved name $name")
        }
        data[name] = value
    }

    fun build(): PgnMetaData {
        if (event.isBlank()) {
            throw ChessException("Event is missing")
        }
        if (site.isBlank()) {
            throw ChessException("Site is missing")
        }
        if (white.isBlank()) {
            throw ChessException("White is missing")
        }
        if (black.isBlank()) {
            throw ChessException("Black is missing")
        }
        return PgnMetaData(
            event = event,
            site = site,
            white = white,
            black = black,
            result = result,
            dateString = date,
            roundString = round,
            data = data
        )
    }

    companion object {
        private val RESERVED_KEYS = arrayOf("FEN", "SetUp")
    }

}