package pluto.chess.engine

import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.PrintStream

class OutputStreamEngineLogger(output: OutputStream) : EngineLogger {

    override var debugEnabled = false

    private val debugWriter = OutputStreamWriter(output)
    private val errorStream = PrintStream(output)

    override fun logDebug(message: String) {
        if (debugEnabled) {
            try {
                debugWriter.append(message).append(NEWLINE).flush()
            } catch (_: IOException) {
            }
        }
    }

    override fun logError(throwable: Throwable) {
        throwable.printStackTrace(errorStream)
        try {
            errorStream.flush()
        } catch (_: IOException) {
        }
    }

    private companion object {
        val NEWLINE = System.getProperty("line.separator")
    }
}