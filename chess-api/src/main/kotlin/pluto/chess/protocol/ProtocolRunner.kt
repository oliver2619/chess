package pluto.chess.protocol

import pluto.chess.ChessException
import pluto.chess.engine.ChessEngine
import pluto.chess.engine.EngineLogger
import pluto.chess.protocol.uci.UciProtocolFactory
import java.io.*

class ProtocolRunner(private val engineFactory: () -> ChessEngine) {

    fun run(input: InputStream, output: OutputStream, logger: EngineLogger) =
        run(InputStreamReader(input), OutputStreamWriter(output), logger)

    fun run(input: Reader, output: Writer, logger: EngineLogger) {
        try {
            var hasRunSuccessfully = false
            while (!hasRunSuccessfully) {
                if (!LineBasedChessProtocol.processInput(input) {
                        hasRunSuccessfully = run(it, input, output, logger)
                    }) {
                    break
                }
            }
        } catch (e: Throwable) {
            logger.logError(e)
        }
    }

    private fun run(command: String, input: Reader, output: Writer, logger: EngineLogger): Boolean {
        for (protocol in protocols) {
            if (protocol.matches(command)) {
                run(protocol, input, output, logger)
                return true
            }
        }
        logger.logError(ChessException("Unknown chess protocol '$command'"))
        return false
    }

    private fun run(protocol: ChessProtocolFactory, input: Reader, output: Writer, logger: EngineLogger) {
        val engine = engineFactory()
        engine.init(logger)
        try {
            protocol.create(input, output, engine, logger).run()
        } finally {
            engine.deinit()
        }
    }

    companion object {
        private val protocols = arrayOf(UciProtocolFactory())
    }

}