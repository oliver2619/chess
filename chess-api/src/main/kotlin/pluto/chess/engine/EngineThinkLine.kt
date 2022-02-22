package pluto.chess.engine

import pluto.chess.move.MoveWithBoard

data class EngineThinkLine(
    val moves: List<MoveWithBoard>,
    val rating: Int
)