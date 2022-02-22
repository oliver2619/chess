package pluto.chess.protocol.uci

import pluto.chess.ChessException
import pluto.chess.engine.EngineParameterConsumer

internal class UciEngineParameterConsumer(
    private val response: UciOptionsResponseBuilder,
    private val mapper: UciEngineParameterValueMapper
) : EngineParameterConsumer {

    override fun action(name: String) {
        checkName(name)
        response.button(name)
        mapper.registerButton(name)
    }

    override fun booleanParameter(name: String, defaultValue: Boolean) {
        checkName(name)
        response.boolean(name, defaultValue)
        mapper.registerBoolean(name)
    }

    override fun choiceParameter(name: String, values: List<String>, defaultValue: String) {
        checkName(name)
        response.choice(name, values, defaultValue)
        mapper.registerChoice(name, values)
    }

    override fun numberParameter(name: String, minValue: Int?, maxValue: Int?, defaultValue: Int) {
        checkName(name)
        response.number(name, minValue, maxValue, defaultValue)
        mapper.registerNumber(name, minValue, maxValue)
    }

    override fun stringParameter(name: String, defaultValue: String) {
        checkName(name)
        response.string(name, defaultValue)
        mapper.registerString(name)
    }

    private fun checkName(name: String) {
        if (name.startsWith("UCI_") || RESERVED_NAMES.contains(name)) {
            throw ChessException("Parameter name '$name' is reserved")
        }
    }

    companion object {

        private val RESERVED_NAMES = setOf("Hash", "NalimovPath", "NalimovCache", "Ponder", "OwnBook", "MultiPV")
    }
}