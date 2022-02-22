package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class DefaultMove(figureColor: FigureColor, figureType: FigureType, startField: Field, targetField: Field) :
    Move(figureColor, figureType, startField, targetField) {

    private val startClearMask = startField.toMask().inv()
    private val targetInsertMask = targetField.toMask()
    private val targetClearMask = targetField.toMask().inv()
    private val clearFlags = calculateClearFlags(startField, targetField)

    override fun isCapturing(board: Board) = board.isFigureOnField(opponentColor, targetField)

    override fun isNotBlocked(board: Board) = !board.isFigureOnField(figureColor, targetField)

    override fun isValueChanging(board: Board) = board.isFigureOnField(opponentColor, targetField)

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        if ((colorMasks[opponentColor.ordinal] and targetInsertMask).isNotEmpty()) {
            colorMasks[opponentColor.ordinal] = colorMasks[opponentColor.ordinal] and targetClearMask
            figureMasks[FigureType.PAWN.ordinal] = figureMasks[FigureType.PAWN.ordinal] and targetClearMask
            figureMasks[FigureType.KNIGHT.ordinal] = figureMasks[FigureType.KNIGHT.ordinal] and targetClearMask
            figureMasks[FigureType.BISHOP.ordinal] = figureMasks[FigureType.BISHOP.ordinal] and targetClearMask
            figureMasks[FigureType.ROOK.ordinal] = figureMasks[FigureType.ROOK.ordinal] and targetClearMask
            figureMasks[FigureType.QUEEN.ordinal] = figureMasks[FigureType.QUEEN.ordinal] and targetClearMask
            flagsFiftyMoves[1] = 0
        } else {
            ++flagsFiftyMoves[1]
        }
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and startClearMask) or targetInsertMask
        figureMasks[figureType.ordinal] = (figureMasks[figureType.ordinal] and startClearMask) or targetInsertMask
        flagsFiftyMoves[0] = flagsFiftyMoves[0] and clearFlags
    }
    }