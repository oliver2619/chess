package pluto.chess.engine

interface EngineParameterConsumer {

    fun action(name: String)

    fun booleanParameter(name: String, defaultValue: Boolean)

    fun choiceParameter(name: String, values: List<String>, defaultValue: String)

    fun numberParameter(name: String, minValue: Int?, maxValue: Int?, defaultValue: Int)

    fun stringParameter(name: String, defaultValue: String)
}