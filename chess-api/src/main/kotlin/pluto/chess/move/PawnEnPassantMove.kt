package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class PawnEnPassantMove(figureColor: FigureColor, startField: Field, targetField: Field) : Move(
    figureColor, FigureType.PAWN, startField,
    targetField
) {

    private val clearMask =
        (startField or if (figureColor == FigureColor.WHITE) targetField.shiftRow(-1) else targetField.shiftRow(1)).inv()
    private val insertMask = targetField.toMask()
    private val checkFlags = Flags.enPassant(targetField.line)
    private val clearFlags = Flags.EN_PASSANT.inv()

    override fun getSeverity(previousBoard: Board, currentBoard: Board) = MoveSeverity.VALUE_CHANGING

    override fun isValueChanging(board: Board) = true

    override fun isCapturing(board: Board) = true

    override fun isNotBlocked(board: Board) = (board.flags and checkFlags) == checkFlags

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and clearMask) or insertMask
        colorMasks[opponentColor.ordinal] = colorMasks[opponentColor.ordinal] and clearMask
        figureMasks[FigureType.PAWN.ordinal] = (figureMasks[FigureType.PAWN.ordinal] and clearMask) or insertMask
        flagsFiftyMoves[0] = flagsFiftyMoves[0] and clearFlags
        flagsFiftyMoves[1] = 0
    }
}