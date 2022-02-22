package pluto.chess.engine

import pluto.chess.move.Move

data class EngineSearchOptions(
    val totalTimeMs: Long? = null,
    val movesUntilNextTimeControl: Int? = null,
    val timeMsIncreasePerMove: Long = 0L,
    val allowedMoves: List<Move>? = null,
    val maxDepth: Int? = null,
    val maxNodes: Int? = null
)
