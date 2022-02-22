package pluto.chess.protocol.uci

import pluto.chess.engine.ChessEngine
import pluto.chess.engine.EngineLogger
import pluto.chess.protocol.ChessProtocolFactory
import java.io.Reader
import java.io.Writer
import java.util.regex.Pattern

internal class UciProtocolFactory : ChessProtocolFactory {

    override fun matches(helo: String): Boolean = HELO_PATTERN.matcher(helo).matches()

    override fun create(reader: Reader, writer: Writer, engine: ChessEngine, logger: EngineLogger): Runnable =
        UciProtocol(reader, writer, engine, logger)

    private companion object {

        private val HELO_PATTERN = Pattern.compile("^uci\$")
    }
}