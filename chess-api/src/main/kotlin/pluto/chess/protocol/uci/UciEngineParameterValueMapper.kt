package pluto.chess.protocol.uci

import pluto.chess.ChessException
import java.util.function.Function

internal class UciEngineParameterValueMapper {

    private val mapper = HashMap<String, Function<String, Any>>()

    fun registerButton(name: String) {
        checkExists(name)
        mapper.put(name) {}
    }

    fun registerBoolean(name: String) {
        checkExists(name)
        mapper.put(name) { it == "true" }
    }

    fun registerChoice(name: String, values: List<String>) {
        checkExists(name)
        mapper.put(name) {
            if (values.contains(it)) {
                it
            } else {
                throw ChessException("Value '$it' for parameter '$name' not allowed")
            }
        }
    }

    fun registerNumber(name: String, minValue: Int?, maxValue: Int?) {
        checkExists(name)
        mapper.put(name) {
            val intValue = it.toInt()
            if ((minValue != null && intValue < minValue) || (maxValue != null && intValue > maxValue)) {
                throw ChessException("Value '$it' for parameter '$name' out of range")
            }
            intValue
        }
    }

    fun registerString(name: String) {
        checkExists(name)
        mapper.put(name) { it }
    }

    fun map(name: String, value: String): Any {
        val converter = mapper[name] ?: throw ChessException("Parameter '$name' is not registered")
        return converter.apply(value)
    }

    private fun checkExists(name: String) {
        if (mapper.contains(name)) {
            throw ChessException("Parameter '$name' already registered")
        }
    }
}