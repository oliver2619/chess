package pluto.chess.protocol.uci

import java.io.Writer

internal class UciResponseWriter(private val writer: Writer) {

    val info: UciInfoResponseBuilder
        get() = UciInfoResponseBuilder(writer)

    val options = UciOptionsResponseBuilder(writer)

    fun bestMove(move: String, ponder: String? = null) {
        writer.append("bestmove ").append(move)
        if (ponder != null) {
            writer.append(" ponder ").append(ponder)
        }
        writer.append(System.lineSeparator()).flush()
    }

    fun id(name: String, author: String) {
        writer.append("id name ").append(name).append(System.lineSeparator())
            .append("id author ").append(author).append(System.lineSeparator())
            .flush()
    }

    fun readyOk() {
        writer.append("readyok").append(System.lineSeparator()).flush()
    }

    fun uciok() {
        writer.append("uciok").append(System.lineSeparator()).flush()
    }
}