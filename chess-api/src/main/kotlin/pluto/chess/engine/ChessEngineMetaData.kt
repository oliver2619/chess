package pluto.chess.engine

interface ChessEngineMetaData {

    val name: String
    val author: String
    val hasOwnOpeningBook: Boolean
    val supportsPermanentBrain: Boolean

    fun listParameters(consumer: EngineParameterConsumer)
}