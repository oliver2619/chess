package pluto.chess.move

import pluto.chess.board.Board
import pluto.chess.board.Field
import pluto.chess.board.Flags
import pluto.chess.board.Mask
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

internal class CastleMove(figureColor: FigureColor, startField: Field, targetField: Field) : Move(
    figureColor, FigureType.KING, startField, targetField
) {

    private val rookInsertField =
        if (targetField.value > startField.value) targetField.shiftLine(-1) else targetField.shiftLine(1)

    private val startClearMask =
        (startField or (if (targetField.value > startField.value) startField.shiftLine(3) else startField.shiftLine(-4))).inv()

    private val targetInsertMask = targetField or rookInsertField

    private val kingInsertMask = targetField.toMask()

    private val rookInsertMask = rookInsertField.toMask()

    private val clearFlags =
        Flags.EN_PASSANT.inv() and if (figureColor == FigureColor.WHITE) Flags.CASTLE_WHITE_BOTH.inv() else Flags.CASTLE_BLACK_BOTH.inv()

    private val checkFlags = if (figureColor == FigureColor.WHITE) {
        if (targetField.value > startField.value) Flags.CASTLE_WHITE_KING_SIDE else Flags.CASTLE_WHITE_QUEEN_SIDE
    } else {
        if (targetField.value > startField.value) Flags.CASTLE_BLACK_KING_SIDE else Flags.CASTLE_BLACK_QUEEN_SIDE
    }

    private val checkEmptyMask = if (figureColor == FigureColor.WHITE) {
        if (targetField.value > startField.value) Field.F._1 or Field.G._1 else (Field.D._1 or Field.C._1 or Field.B._1)
    } else {
        if (targetField.value > startField.value) Field.F._8 or Field.G._8 else (Field.D._8 or Field.C._8 or Field.B._8)
    }

    override fun getSeverity(previousBoard: Board, currentBoard: Board) = MoveSeverity.ATTACKING

    override fun isCapturing(board: Board) = false

    override fun isNotBlocked(board: Board) =
        (board.flags and checkFlags) == checkFlags && (board.getColorMask() and checkEmptyMask).isEmpty() && !board.isFieldAttackedBy(
            startField,
            opponentColor
        ) && !board.isFieldAttackedBy(rookInsertField, opponentColor)

    override fun isValueChanging(board: Board) = false

    override fun move(colorMasks: Array<Mask>, figureMasks: Array<Mask>, flagsFiftyMoves: Array<Int>) {
        colorMasks[figureColor.ordinal] = (colorMasks[figureColor.ordinal] and startClearMask) or targetInsertMask
        figureMasks[FigureType.KING.ordinal] =
            (figureMasks[FigureType.KING.ordinal] and startClearMask) or kingInsertMask
        figureMasks[FigureType.ROOK.ordinal] =
            (figureMasks[FigureType.ROOK.ordinal] and startClearMask) or rookInsertMask
        flagsFiftyMoves[0] = flagsFiftyMoves[0] and clearFlags
        ++flagsFiftyMoves[1]
    }

    override fun toString(): String {
        return if (targetField.value > startField.value) "O-O" else "O-O-O"
    }

    companion object {

    }
}