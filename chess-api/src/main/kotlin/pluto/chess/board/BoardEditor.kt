package pluto.chess.board

import pluto.chess.ChessException
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

class BoardEditor private constructor(private val colorMasks: Array<Mask>, private val figureMasks: Array<Mask>) {

    fun canInsertFigure(color: FigureColor, type: FigureType): Boolean {
        if (type == FigureType.KING) {
            return (colorMasks[color.ordinal] and figureMasks[type.ordinal]).isEmpty()
        }
        val count = FigureType.values().map { getFigureCount(color, it) }.toTypedArray()
        ++count[type.ordinal]
        if (count[FigureType.PAWN.ordinal] > 8) {
            return false
        }
        var convertedPawns = 0
        if (count[FigureType.KNIGHT.ordinal] > 2) {
            convertedPawns += count[FigureType.KNIGHT.ordinal] - 2
        }
        if (count[FigureType.BISHOP.ordinal] > 2) {
            convertedPawns += count[FigureType.BISHOP.ordinal] - 2
        }
        if (count[FigureType.ROOK.ordinal] > 2) {
            convertedPawns += count[FigureType.ROOK.ordinal] - 2
        }
        if (count[FigureType.QUEEN.ordinal] > 1) {
            convertedPawns += count[FigureType.QUEEN.ordinal] - 1
        }
        return count[FigureType.PAWN.ordinal] + convertedPawns <= 8
    }

    fun canPlaceFigure(type: FigureType, field: Field): Boolean =
        (type != FigureType.PAWN || (field.row in 1..6)) && !isFigureOnField(field)

    fun insertFigure(color: FigureColor, type: FigureType, field: Field): BoardEditor {
        if (!canPlaceFigure(type, field)) {
            throw ChessException("Can't insert figure of type ${type.name} at field $field")
        }
        if (!canInsertFigure(color, type)) {
            throw ChessException("No more $color figures of type ${type.name} can be inserted")
        }
        colorMasks[color.ordinal] = colorMasks[color.ordinal] or field.toMask()
        figureMasks[type.ordinal] = figureMasks[type.ordinal] or field.toMask()
        return this
    }

    fun moveFigure(start: Field, target: Field): BoardEditor {
        if (!isFigureOnField(start)) {
            throw ChessException("There is no figure on field $start")
        }
        val type = getFigureType(start)
        if (!canPlaceFigure(type, target)) {
            throw ChessException("Can't move figure of type ${type.name} to field $target")
        }
        val startMask = start.toMask()
        val clearMask = start.toMask().inv()
        val insertMask = target.toMask()
        FigureColor.values().forEach {
            if ((colorMasks[it.ordinal] and startMask).isNotEmpty()) {
                colorMasks[it.ordinal] = (colorMasks[it.ordinal] and clearMask) or insertMask
            }
        }
        FigureType.values().forEach {
            if ((figureMasks[it.ordinal] and startMask).isNotEmpty()) {
                figureMasks[it.ordinal] = (figureMasks[it.ordinal] and clearMask) or insertMask
            }
        }
        return this
    }

    fun removeFigure(field: Field): BoardEditor {
        if (!isFigureOnField(field)) {
            throw ChessException("There is no figure on field $field")
        }
        if ((field.toMask() and figureMasks[FigureType.KING.ordinal]).isNotEmpty()) {
            throw ChessException("King can't be removed")
        }
        val clearMask = field.toMask().inv()
        FigureColor.values().forEach { colorMasks[it.ordinal] = colorMasks[it.ordinal] and clearMask }
        FigureType.values().forEach { figureMasks[it.ordinal] = figureMasks[it.ordinal] and clearMask }
        return this
    }

    fun isFigureOnField(field: Field): Boolean {
        return ((colorMasks[0] or colorMasks[1]) and field.toMask()).isNotEmpty()
    }

    fun canResumeWithFlags(colorOnTurn: FigureColor, flags: Int): Boolean {
        if ((flags and getAvailableCastleFlagsToResume()) != (flags and Flags.CASTLE_ALL)) {
            return false
        }
        if ((flags and Flags.EN_PASSANT_ENABLED) == Flags.EN_PASSANT_ENABLED) {
            val line = flags and Flags.EN_PASSANT_LINE_MASK
            if (!getEnPassantLines(colorOnTurn).contains(line)) {
                return false
            }
        }
        return true
    }

    fun getAvailableCastleFlagsToResume(): Int {
        var ret = 0
        val kingMask = figureMasks[FigureType.KING.ordinal]
        val rookMask = figureMasks[FigureType.ROOK.ordinal]
        val whiteMask = colorMasks[FigureColor.WHITE.ordinal]
        val blackMask = colorMasks[FigureColor.BLACK.ordinal]
        if ((kingMask and whiteMask and Field.E._1.toMask()).isNotEmpty()) {
            if ((rookMask and whiteMask and Field.A._1.toMask()).isNotEmpty()) {
                ret = ret or Flags.CASTLE_WHITE_QUEEN_SIDE
            }
            if ((rookMask and whiteMask and Field.H._1.toMask()).isNotEmpty()) {
                ret = ret or Flags.CASTLE_WHITE_KING_SIDE
            }
        }
        if ((kingMask and blackMask and Field.E._8.toMask()).isNotEmpty()) {
            if ((rookMask and blackMask and Field.A._8.toMask()).isNotEmpty()) {
                ret = ret or Flags.CASTLE_BLACK_QUEEN_SIDE
            }
            if ((rookMask and blackMask and Field.H._8.toMask()).isNotEmpty()) {
                ret = ret or Flags.CASTLE_BLACK_KING_SIDE
            }
        }
        return ret
    }

    fun getEnPassantLines(colorOnTurn: FigureColor): Array<Int> {
        val pawnMask = colorMasks[colorOnTurn.inv().ordinal] and figureMasks[FigureType.PAWN.ordinal]
        val colorMask = colorMasks[0] or colorMasks[1]
        return (0..7).filter { line ->
            val fieldWithPawn: Field
            val emptyMask: Mask
            if (colorOnTurn == FigureColor.WHITE) {
                fieldWithPawn = Field(line + 4 * 8)
                emptyMask = Field(line + 5 * 8).toMask() or Field(line + 6 * 8)
            } else {
                fieldWithPawn = Field(line + 3 * 8)
                emptyMask = Field(line + 2 * 8).toMask() or Field(line + 1 * 8)
            }
            (pawnMask and fieldWithPawn.toMask()).isNotEmpty() && (colorMask and emptyMask).isEmpty()
        }.toTypedArray()
    }

    fun resume(colorOnTurn: FigureColor) = resume(colorOnTurn, getAvailableCastleFlagsToResume())

    fun resume(colorOnTurn: FigureColor, flags: Int) = resume(colorOnTurn, flags, 0)

    fun resume(colorOnTurn: FigureColor, flags: Int, fiftyMoves: Int): Board {
        if ((colorMasks[FigureColor.WHITE.ordinal] and figureMasks[FigureType.KING.ordinal]).isEmpty()) {
            throw ChessException("White king is missing")
        }
        if ((colorMasks[FigureColor.BLACK.ordinal] and figureMasks[FigureType.KING.ordinal]).isEmpty()) {
            throw ChessException("Black king is missing")
        }
        if (fiftyMoves !in 0..50) {
            throw ChessException("Fifty moves must be in range of 0..50")
        }
        if (!canResumeWithFlags(colorOnTurn, flags)) {
            throw ChessException("Situation can't be resumed by $colorOnTurn with flags $flags")
        }
        val ret = Board(colorOnTurn, colorMasks, figureMasks, flags, fiftyMoves)
        if (ret.isKingAttacked(colorOnTurn.inv())) {
            throw ChessException("Opponent's king is attacked")
        }
        return ret
    }

    private fun getFigureType(field: Field): FigureType {
        val mask = field.toMask()
        for (type in FigureType.values()) {
            if ((figureMasks[type.ordinal] and mask).isNotEmpty()) {
                return type
            }
        }
        throw ChessException("There is no figure on field $field")
    }

    private fun getFigureCount(color: FigureColor, type: FigureType) =
        (colorMasks[color.ordinal] and figureMasks[type.ordinal]).fieldCount()

    companion object {

        fun empty(): BoardEditor {
            val colorMasks = FigureColor.values().map { Mask() }.toTypedArray()
            val figureMasks = FigureType.values().map { Mask() }.toTypedArray()
            return BoardEditor(colorMasks, figureMasks)
        }

        fun withDefaultKings(): BoardEditor = withKings(Field.E._1, Field.E._8)

        fun withKings(whiteKing: Field = Field.E._1, blackKing: Field = Field.E._8): BoardEditor {
            if (whiteKing == blackKing) {
                throw ChessException("White king and black king are on the same field")
            }
            val colorMasks = FigureColor.values().map { Mask() }.toTypedArray()
            val figureMasks = FigureType.values().map { Mask() }.toTypedArray()
            colorMasks[FigureColor.WHITE.ordinal] = whiteKing.toMask()
            colorMasks[FigureColor.BLACK.ordinal] = blackKing.toMask()
            figureMasks[FigureType.KING.ordinal] = whiteKing.toMask() or blackKing
            return BoardEditor(colorMasks, figureMasks)
        }

        fun fromBoard(board: Board): BoardEditor {
            val colorMasks = FigureColor.values().map { board.getColorMask(it) }.toTypedArray()
            val figureMasks = FigureType.values().map { board.getFigureMask(it) }.toTypedArray()
            return BoardEditor(colorMasks, figureMasks)
        }

        fun fromStart(): BoardEditor {
            val colorMasks = FigureColor.values().map { Mask() }.toTypedArray()
            val figureMasks = FigureType.values().map { Mask() }.toTypedArray()
            Board.initStartPositionMasks(colorMasks, figureMasks)
            return BoardEditor(colorMasks, figureMasks)
        }
    }
}