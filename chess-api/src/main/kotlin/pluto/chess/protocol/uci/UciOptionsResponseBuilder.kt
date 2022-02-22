package pluto.chess.protocol.uci

import java.io.Writer

internal class UciOptionsResponseBuilder(private val writer: Writer) {

    fun button(name: String) {
        writeOption(name, "button") { }
    }

    fun boolean(name: String, defaultValue: Boolean? = null) {
        writeOption(name, "check") {
            defaultValue?.let { writer.append(" default ").append(it.toString()) }
        }
    }

    fun choice(name: String, values: List<String>, defaultValue: String? = null) {
        writeOption(name, "combo") {
            values.forEach { writer.append(" var ").append(it) }
            defaultValue?.let { writer.append(" default ").append(it) }
        }
    }

    fun number(name: String, minValue: Int?, maxValue: Int?, defaultValue: Int? = null) {
        writeOption(name, "spin") {
            minValue?.let { writer.append(" min ").append(it.toString()) }
            maxValue?.let { writer.append(" max ").append(it.toString()) }
            defaultValue?.let { writer.append(" default ").append(it.toString()) }
        }
    }

    fun string(name: String, defaultValue: String? = null) {
        writeOption(name, "string") {
            defaultValue?.let { writer.append(" default ").append(it) }
        }
    }

    private fun writeOption(name: String, type: String, other: () -> Unit) {
        writer.append("option name ").append(name).append(" type ").append(type)
        other()
        writer.append(System.lineSeparator()).flush()
    }
}