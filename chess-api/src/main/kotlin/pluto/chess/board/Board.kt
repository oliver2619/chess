package pluto.chess.board

import pluto.chess.figure.Figure
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import pluto.chess.figure.Figures
import pluto.chess.move.Conversion
import pluto.chess.move.Move
import pluto.chess.move.MoveWithBoard

class Board internal constructor(
    val colorOnTurn: FigureColor,
    private val colorMasks: Array<Mask>,
    private val figureMasks: Array<Mask>,
    val flags: Int,
    val fiftyMoves: Int
) {

    fun getColorMask() = colorMasks[0] or colorMasks[1]

    fun getColorMask(color: FigureColor) = colorMasks[color.ordinal]

    fun getFigureMask(type: FigureType) = figureMasks[type.ordinal]

    fun getFigureOnField(field: Field): Figure? {
        val mask = field.toMask()
        for (color in FigureColor.values()) {
            if ((colorMasks[color.ordinal] and mask).isNotEmpty()) {
                for (type in FigureType.values()) {
                    if ((figureMasks[type.ordinal] and mask).isNotEmpty()) {
                        return Figures.getFigure(color, type, field)
                    }
                }
            }
        }
        return null
    }

    fun getFigureOnField(color: FigureColor, type: FigureType, field: Field): Figure? =
        if ((figureMasks[type.ordinal] and colorMasks[color.ordinal] and field.toMask()).isNotEmpty()) Figures.getFigure(
            color,
            type,
            field
        ) else null

    fun isFigureOnField(field: Field): Boolean =
        ((colorMasks[0] or colorMasks[1]) and field.toMask()).isNotEmpty()

    fun isFigureOnField(color: FigureColor, field: Field): Boolean =
        (colorMasks[color.ordinal] and field.toMask()).isNotEmpty()

    fun isFieldAttackedBy(field: Field, attackingColor: FigureColor): Boolean {
        val attackedColor = attackingColor.inv()
        val attackingColorMask = colorMasks[attackingColor.ordinal]
        // pawn
        if ((Masks.pawnAttack(
                field,
                attackedColor
            ) and attackingColorMask and figureMasks[FigureType.PAWN.ordinal]).isNotEmpty()
        ) {
            return true
        }
        // knight
        if ((Masks.knightAttack(field) and attackingColorMask and figureMasks[FigureType.KNIGHT.ordinal]).isNotEmpty()) {
            return true
        }
        // king
        if ((Masks.kingAttack(field) and attackingColorMask and figureMasks[FigureType.KING.ordinal]).isNotEmpty()) {
            return true
        }
        // line
        val attackedColorMask = colorMasks[attackedColor.ordinal]
        val obstacleMask = attackingColorMask or attackedColorMask
        val queenMask = figureMasks[FigureType.QUEEN.ordinal]
        val rookOrQueenMask = (figureMasks[FigureType.ROOK.ordinal] or queenMask) and attackingColorMask
        val bishopOrQueenMask = (figureMasks[FigureType.BISHOP.ordinal] or queenMask) and attackingColorMask
        val rookOrQueenObstacleMask = obstacleMask and rookOrQueenMask.inv()
        val bishopOrQueenObstacleMask = obstacleMask and bishopOrQueenMask.inv()
        var rayMask = Masks.ray(field, Ray.N)
        if (isLowFieldAttacked(rookOrQueenMask and rayMask, rookOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.NE)
        if (isLowFieldAttacked(bishopOrQueenMask and rayMask, bishopOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.E)
        if (isLowFieldAttacked(rookOrQueenMask and rayMask, rookOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.SE)
        if (isHighFieldAttacked(bishopOrQueenMask and rayMask, bishopOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.S)
        if (isHighFieldAttacked(rookOrQueenMask and rayMask, rookOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.SW)
        if (isHighFieldAttacked(bishopOrQueenMask and rayMask, bishopOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.W)
        if (isHighFieldAttacked(rookOrQueenMask and rayMask, rookOrQueenObstacleMask and rayMask)) {
            return true
        }
        rayMask = Masks.ray(field, Ray.NW)
        return isLowFieldAttacked(bishopOrQueenMask and rayMask, bishopOrQueenObstacleMask and rayMask)
    }

    private fun isLowFieldAttacked(attackMask: Mask, obstacleMask: Mask): Boolean {
        val a = attackMask.lowestBit()
        val o = obstacleMask.lowestBit()
        return if (o.value > 0UL && a.value > 0UL) a.value < o.value else a.value > 0UL
    }

    private fun isHighFieldAttacked(attackMask: Mask, obstacleMask: Mask): Boolean =
        attackMask.value > obstacleMask.value

    fun isKingAttacked(attackedKingColor: FigureColor): Boolean {
        return isFieldAttackedBy(
            (colorMasks[attackedKingColor.ordinal] and figureMasks[FigureType.KING.ordinal]).toField(),
            attackedKingColor.inv()
        )
    }

    fun isChecked(): Boolean {
        return isKingAttacked(colorOnTurn)
    }

    fun hasAnyMoves(): Boolean {
        return !findMoves { _, _ -> MoveConsumerResult.CANCEL }
    }

    fun findMove(startField: Field, targetField: Field, conversion: Conversion? = null): MoveWithBoard? =
        getFigureOnField(startField)?.findMove(this, targetField, conversion)

    fun findMove(
        figureType: FigureType,
        startField: Field,
        targetField: Field,
        conversion: Conversion? = null
    ): MoveWithBoard? =
        getFigureOnField(colorOnTurn, figureType, startField)?.findMove(this, targetField, conversion)

    fun findMoves(): List<MoveWithBoard> {
        var ret = mutableListOf<MoveWithBoard>()
        findMoves { move, board ->
            ret.add(MoveWithBoard(move, board))
            MoveConsumerResult.CONTINUE_ALL
        }
        return ret
    }

    fun findMoves(consumer: MoveConsumer): Boolean {
        val colorMask = colorMasks[colorOnTurn.ordinal]
        var ret = MoveConsumerResult.CONTINUE_ALL
        for (figureType in FigureType.values()) {
            val mask = colorMask and figureMasks[figureType.ordinal]
            if (ret == MoveConsumerResult.CONTINUE_VALUE_CHANGING) {
                if (!mask.everyField {
                        Figures.getFigure(colorOnTurn, figureType, it).getValueChangingMoves(this, consumer)
                    }) {
                    return false
                }
            } else {
                ret = ret combine mask.forEachField {
                    Figures.getFigure(colorOnTurn, figureType, it).getMoves(this, consumer)
                }
                if (ret == MoveConsumerResult.CANCEL) {
                    return false
                }
            }
        }
        return true
    }

    fun findMovesTo(
        figureType: FigureType,
        targetField: Field,
        conversion: Conversion? = null,
        consumer: (MoveWithBoard) -> Unit
    ) {
        val startMask = when (figureType) {
            FigureType.PAWN -> {
                val dir = if (colorOnTurn == FigureColor.WHITE) -1 else 1
                val ret = Masks.pawnAttack(targetField, colorOnTurn.inv()) or targetField.shiftRow(dir)
                when {
                    targetField.row == 3 && colorOnTurn == FigureColor.WHITE -> ret or targetField.shiftRow(dir * 2)
                    targetField.row == 4 && colorOnTurn == FigureColor.BLACK -> ret or targetField.shiftRow(dir * 2)
                    else -> ret
                }
            }
            FigureType.KNIGHT -> Masks.knightAttack(targetField)
            FigureType.BISHOP -> Masks.bishopAttack(targetField)
            FigureType.ROOK -> Masks.rookAttack(targetField)
            FigureType.QUEEN -> Masks.queenAttack(targetField)
            FigureType.KING -> {
                val ret = Masks.kingAttack(targetField)
                when (targetField) {
                    Field.C._1, Field.G._1 -> ret or Field.E._1
                    Field.C._8, Field.G._8 -> ret or Field.E._8
                    else -> ret
                }
            }
        }
        (colorMasks[colorOnTurn.ordinal] and figureMasks[figureType.ordinal] and startMask).streamFields()
            .forEach { field ->
                val moveWithBoard =
                    Figures.getFigure(colorOnTurn, figureType, field).findMove(this, targetField, conversion)
                if (moveWithBoard != null) {
                    consumer(moveWithBoard)
                }
            }
    }

    fun moved(move: Move): Board {
        val newColorMasks = colorMasks.copyOf()
        val newFigureMasks = figureMasks.copyOf()
        val newFlagsFiftyMoves = arrayOf(flags, fiftyMoves)
        move.move(newColorMasks, newFigureMasks, newFlagsFiftyMoves)
        return Board(colorOnTurn.inv(), newColorMasks, newFigureMasks, newFlagsFiftyMoves[0], newFlagsFiftyMoves[1])
    }

    fun hasSufficientMaterial(): Boolean {
        if (figureMasks[FigureType.QUEEN.ordinal].value != 0UL || figureMasks[FigureType.ROOK.ordinal].value != 0UL || figureMasks[FigureType.PAWN.ordinal].value != 0UL) {
            return true
        }
        val bishopOrKnight = figureMasks[FigureType.KNIGHT.ordinal] or figureMasks[FigureType.BISHOP.ordinal]
        return (bishopOrKnight and colorMasks[0]).fieldCount() > 1 || (bishopOrKnight and colorMasks[1]).fieldCount() > 1
    }

    fun isRemisByMaterialOrFiftyMoves(): Boolean {
        // TODO move repetition
        return this.fiftyMoves >= 50 || !hasSufficientMaterial()
    }

    companion object BoardBuilder {

        private val startPosition: Board by lazy {
            val colorMasks = FigureColor.values().map { Mask() }.toTypedArray()
            val figureMasks = FigureType.values().map { Mask() }.toTypedArray()
            initStartPositionMasks(colorMasks, figureMasks)
            Board(FigureColor.WHITE, colorMasks, figureMasks, Flags.CASTLE_ALL, 0)
        }

        fun newGame(): Board = startPosition

        internal fun initStartPositionMasks(colorMasks: Array<Mask>, figureMasks: Array<Mask>): Unit {
            colorMasks[FigureColor.WHITE.ordinal] = Masks.row(0) or Masks.row(1)
            colorMasks[FigureColor.BLACK.ordinal] = Masks.row(6) or Masks.row(7)
            figureMasks[FigureType.PAWN.ordinal] = Masks.row(1) or Masks.row(6)
            figureMasks[FigureType.KNIGHT.ordinal] =
                Field.B._1.toMask() or Field.G._1 or Field.B._8 or Field.G._8
            figureMasks[FigureType.BISHOP.ordinal] =
                Field.C._1.toMask() or Field.F._1 or Field.C._8 or Field.F._8
            figureMasks[FigureType.ROOK.ordinal] =
                Field.A._1.toMask() or Field.H._1 or Field.A._8 or Field.H._8
            figureMasks[FigureType.QUEEN.ordinal] = Field.D._1.toMask() or Field.D._8
            figureMasks[FigureType.KING.ordinal] = Field.E._1.toMask() or Field.E._8
        }
    }
}