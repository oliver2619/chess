package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class PawnNonCapturingMove(
    figureColor: FigureColor, startField: Field,
    conversion: Conversion? = null
) : Move(
    figureColor,
    FigureType.PAWN,
    startField,
    if (figureColor == FigureColor.WHITE) startField shiftRow 1 else (startField shiftRow -1),
    conversion
) {

    private val startClearMask = startField.toMask().inv()
    private val targetInsertMask = targetField.toMask()
    private val targetInsertFigureIndex = conversion?.figure?.ordinal ?: FigureType.PAWN.ordinal
    private val clearFlags = Flags.EN_PASSANT.inv()
    private val valueChanging = conversion != null

    override fun isValueChanging(board: Board) = valueChanging

    override fun isCapturing(board: Board) = false

    override fun isNotBlocked(board: Board) = !board.isFigureOnField(targetField)

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and startClearMask) or targetInsertMask
        figureMasks[FigureType.PAWN.ordinal] = figureMasks[FigureType.PAWN.ordinal] and startClearMask
        figureMasks[targetInsertFigureIndex] = figureMasks[targetInsertFigureIndex] or targetInsertMask
        flagsFiftyMoves[0] = flagsFiftyMoves[0] and clearFlags
        flagsFiftyMoves[1] = 0
    }
}