package pluto.chess.engine

interface EngineLogger {

    var debugEnabled: Boolean

    fun logDebug(message: String)

    fun logError(throwable: Throwable)
}