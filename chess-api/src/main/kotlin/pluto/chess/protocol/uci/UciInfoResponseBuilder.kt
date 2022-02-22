package pluto.chess.protocol.uci

import java.io.Writer

internal class UciInfoResponseBuilder(private val writer: Writer) {

    private var depth: Int? = null
    private var nodes: Int? = null
    private var score: String? = null
    private var timeMs: Int? = null
    private var currentMove: String? = null
    private var bestLine: String? = null
    private var string: String? = null

    fun bestLine(moves: List<String>) = apply { bestLine = moves.joinToString(" ") }

    fun currentMove(currentMove: String) = apply { this.currentMove = currentMove }

    fun depthPlies(depth: Int) = apply { this.depth = depth }

    fun nodes(nodes: Int) = apply { this.nodes = nodes }

    // Engine's point of view
    fun scoreCp(score: Int) = apply { this.score = "cp $score" }

    // Engine's point of view otherwise negative, unit is moves and not plies
    fun scoreMate(remainingMoves: Int) = apply { this.score = "mate $remainingMoves" }

    fun string(string: String) = apply { this.string = string }

    fun timeMs(timeMs: Int) = apply { this.timeMs = timeMs }

    fun write() {
        writer.append("info")
        depth?.let { writer.append(" depth ").append(it.toString()) }
        nodes?.let { writer.append(" nodes ").append(it.toString()) }
        timeMs?.let { writer.append(" time ").append(it.toString()) }
        nodes?.let { nodes ->
            timeMs?.let { timeMs ->
                if (timeMs != 0) {
                    val nps = nodes * 1000 / timeMs
                    writer.append(" nps ").append(nps.toString())
                }
            }
        }
        score?.let {writer.append(" score ").append(it)}
        currentMove?.let {writer.append(" currmove ").append(it)}
        bestLine?.let {writer.append(" pv ").append(it)}
        string?.let {writer.append(" string ").append(it)}
        writer.append(System.lineSeparator()).flush()
    }
}