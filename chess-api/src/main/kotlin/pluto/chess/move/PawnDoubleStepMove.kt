package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class PawnDoubleStepMove(figureColor: FigureColor, startField: Field) : Move(
    figureColor,
    FigureType.PAWN,
    startField,
    if (figureColor == FigureColor.WHITE) startField shiftRow 2 else (startField shiftRow -2)
) {

    private val startClearMask = startField.toMask().inv()
    private val targetInsertMask = targetField.toMask()
    private val clearFlags = Flags.EN_PASSANT.inv()
    private val insertFlags = Flags.enPassant(startField.line)

    override fun isCapturing(board: Board) = false

    override fun isNotBlocked(board: Board) = !board.isFigureOnField(targetField)

    override fun isValueChanging(board: Board) = false

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and startClearMask) or targetInsertMask
        figureMasks[FigureType.PAWN.ordinal] =
            (figureMasks[FigureType.PAWN.ordinal] and startClearMask) or targetInsertMask
        // attention: en passant flags could have different lines. Just inserting insertFlags will fail. Old en passant flags must be cleared.
        flagsFiftyMoves[0] = (flagsFiftyMoves[0] and clearFlags) or insertFlags
        flagsFiftyMoves[1] = 0
    }
}