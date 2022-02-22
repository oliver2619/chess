package pluto.chess.protocol

import java.io.Reader

object LineBasedChessProtocol {

    fun processInput(reader: Reader, callback: (command: String) -> Unit): Boolean {
        val cmdBuilder = StringBuilder()
        var read = reader.read()
        while (read != -1) {
            if (read == '\n'.code || read == '\r'.code) {
                if (cmdBuilder.isNotEmpty()) {
                    val cmd = cmdBuilder.toString().trim()
                    if (cmd.isNotEmpty()) {
                        callback(cmd)
                        return true
                    }
                    cmdBuilder.clear()
                }
            } else {
                cmdBuilder.append(read.toChar())
            }
            read = reader.read()
        }
        return false
    }
}