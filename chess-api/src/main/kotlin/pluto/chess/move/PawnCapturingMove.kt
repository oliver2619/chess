package pluto.chess.move

import pluto.chess.ChessException
import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class PawnCapturingMove(
    figureColor: FigureColor,
    startField: Field,
    targetField: Field,
    conversion: Conversion? = null
) : Move(
    figureColor, FigureType.PAWN,
    startField,
    targetField,
    conversion
) {

    private val clearMask = (startField or targetField).inv()
    private val targetInsertMask = targetField.toMask()
    private val clearFlags = calculateClearFlags(startField, targetField)
    private val targetInsertFigureIndex = conversion?.figure?.ordinal ?: FigureType.PAWN.ordinal

    override fun getSeverity(previousBoard: Board, currentBoard: Board): MoveSeverity = MoveSeverity.VALUE_CHANGING

    override fun isNotBlocked(board: Board) = board.isFigureOnField(opponentColor, targetField)

    override fun isValueChanging(board: Board) = true

    override fun isCapturing(board: Board) = true

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and clearMask) or targetInsertMask
        colorMasks[opponentColor.ordinal] = colorMasks[opponentColor.ordinal] and clearMask
        figureMasks[FigureType.PAWN.ordinal] = figureMasks[FigureType.PAWN.ordinal] and clearMask
        figureMasks[FigureType.KNIGHT.ordinal] = figureMasks[FigureType.KNIGHT.ordinal] and clearMask
        figureMasks[FigureType.BISHOP.ordinal] = figureMasks[FigureType.BISHOP.ordinal] and clearMask
        figureMasks[FigureType.ROOK.ordinal] = figureMasks[FigureType.ROOK.ordinal] and clearMask
        figureMasks[FigureType.QUEEN.ordinal] = figureMasks[FigureType.QUEEN.ordinal] and clearMask
        figureMasks[targetInsertFigureIndex] = figureMasks[targetInsertFigureIndex] or targetInsertMask
        flagsFiftyMoves[0] = flagsFiftyMoves[0] and clearFlags
        flagsFiftyMoves[1] = 0
    }
}