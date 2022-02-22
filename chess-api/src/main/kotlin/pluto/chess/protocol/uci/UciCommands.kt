package pluto.chess.protocol.uci

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.move.MoveWithBoard
import pluto.chess.notation.Fen
import java.util.regex.Pattern

private fun interface UciCommand {

    fun execute(params: String?, uciInterface: UciProtocolInterface)
}

private class UciSetOptionCommand : UciCommand {

    private val pattern =
        Pattern.compile("^(?:name\\s+)?(?<name>(?:[^v]|v[^a]|va[^l]|val[^u]|valu[^e])+)(?:value\\s+(?<value>.*))?$")

    override fun execute(params: String?, uciInterface: UciProtocolInterface) {
        val matcher = pattern.matcher(params ?: throw ChessException("Command 'setoption' requires parameters"))
        if (matcher.matches()) {
            uciInterface.setOption(matcher.group("name").trim(), matcher.group("value")?.trim())
        } else {
            throw ChessException("Illegal parameters '$params' for command 'setoption'")
        }
    }

}

private class UciSetPositionCommand : UciCommand {

    private val paramsPattern =
        Pattern.compile("^(?:fen\\s+(?<fen>(?:[^m]|m[^o]|mo[^v]|mov[^e]|move[^s])+)|startpos)\\s*(?:moves\\s*(?<moves>.*)?)?$")
    private val moveSplitPattern = Pattern.compile("\\s+")

    override fun execute(params: String?, uciInterface: UciProtocolInterface) {
        val matcher = paramsPattern.matcher(params ?: throw ChessException("Command 'position' requires parameters"))
        if (!matcher.matches()) {
            throw ChessException("Illegal parameters '$params' for command 'position'")
        }
        val fen = matcher.group("fen")
        val startPosition = if (fen != null) Fen.parseBoard(fen) else Board.newGame()
        val moves = matcher.group("moves").let { if (it != null) parseMoves(it, startPosition) else listOf() }
        uciInterface.setPosition(startPosition, moves)
    }

    private fun parseMoves(moves: String, startPosition: Board): List<MoveWithBoard> {
        var board = startPosition
        val moveArray = moves.trim().split(moveSplitPattern)
        val ret = mutableListOf<MoveWithBoard>()
        for (move in moveArray) {
            val mvb = UciNotation.parse(move, board)
            ret.add(mvb)
            board = mvb.board
        }
        return ret
    }

}

internal object UciCommands {

    private val commandsByName: Map<String, UciCommand> =
        mapOf(
            "debug" to UciCommand { params, uciInterface -> uciInterface.setDebug(params == "true") },
            "isready" to UciCommand { params, uciInterface -> uciInterface.waitForIdle() },
            "setoption" to UciSetOptionCommand(),
            "register" to UciCommand { params, uciInterface -> },
            "ucinewgame" to UciCommand { params, uciInterface -> uciInterface.newGame() },
            "position" to UciSetPositionCommand(),
            "go" to UciCommand { params, uciInterface ->
                uciInterface.go(
                    UciSearchOptions.parse(
                        params ?: throw ChessException("Command 'go' requires parameters")
                    )
                )
            },
            "stop" to UciCommand { params, uciInterface -> uciInterface.stop() },
            "ponderhit" to UciCommand { params, uciInterface -> uciInterface.ponderHit() },
            "quit" to UciCommand { params, uciInterface -> uciInterface.quit() }
        )

    fun execute(command: String, params: String?, uciInterface: UciProtocolInterface) =
        (commandsByName[command] ?: throw ChessException("Unknown command '$command'")).execute(params, uciInterface)
}