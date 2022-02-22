package pluto.chess.protocol

import pluto.chess.engine.ChessEngine
import pluto.chess.engine.EngineLogger
import java.io.Reader
import java.io.Writer

interface ChessProtocolFactory {

    fun matches(helo: String): Boolean

    fun create(reader: Reader, writer: Writer, engine: ChessEngine, logger: EngineLogger): Runnable
}