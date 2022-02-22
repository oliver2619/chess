package pluto.chess.board

import pluto.chess.figure.FigureColor

object Masks {

    private val LINE = Array(8) { Mask(0x0101_0101_0101_0101UL shl it) }

    private val ROW = Array(8) { Mask(0xffUL shl (it * 8)) }

    private val RAY = Array(64) { f ->
        val field = Field(f)
        Ray.values().map { ray ->
            var ret = Mask()
            for (i in 1..7) {
                val newLine = field.line + i * ray.dx
                val newRow = field.row + i * ray.dy
                if (newLine in 0..7 && newRow in 0..7) {
                    ret = ret or Field(newLine + newRow * 8)
                }
            }
            ret
        }.toTypedArray()
    }

    private val PAWN_ATTACK = Array(64) { f ->
        val field = Field(f)
        val ret = arrayOf(Mask(), Mask())
        if(field.line > 0){
            if(field.row < 7){
                ret[FigureColor.WHITE.ordinal] = ret[FigureColor.WHITE.ordinal] or Field(f + 7).toMask()
            }
            if(field.row > 0){
                ret[FigureColor.BLACK.ordinal] = ret[FigureColor.BLACK.ordinal] or Field(f - 9).toMask()
            }
        }
        if(field.line < 7){
            if(field.row < 7){
                ret[FigureColor.WHITE.ordinal] = ret[FigureColor.WHITE.ordinal] or Field(f + 9).toMask()
            }
            if(field.row > 0){
                ret[FigureColor.BLACK.ordinal] = ret[FigureColor.BLACK.ordinal] or Field(f - 7).toMask()
            }
        }
        ret
    }

    private val KNIGHT_ATTACK = Array(64) { f ->
        val field = Field(f)
        var ret = Mask()
        for (dx in -2..2) {
            if (dx != 0 && field.line + dx in 0..7) {
                for (dy in -2..2) {
                    if (dy != 0 && field.row + dy in 0..7) {
                        if(Math.abs(dx) != Math.abs(dy)) {
                            ret = ret or Field(f + dx + dy * 8)
                        }
                    }
                }
            }
        }
        ret
    }

    private val BISHOP_ATTACK = Array(64){
        val rays = RAY[it]
        rays[Ray.NE.ordinal] or rays[Ray.SE.ordinal] or rays[Ray.NW.ordinal] or rays[Ray.SW.ordinal]
    }

    private val ROOK_ATTACK = Array(64){
        val rays = RAY[it]
        rays[Ray.N.ordinal] or rays[Ray.S.ordinal] or rays[Ray.E.ordinal] or rays[Ray.W.ordinal]
    }

    private val QUEEN_ATTACK = Array(64){
        BISHOP_ATTACK[it] or ROOK_ATTACK[it]
    }

    private val KING_ATTACK = Array(64) { f ->
        val field = Field(f)
        var ret = Mask()
        for (dx in -1..1) {
            if (field.line + dx in 0..7) {
                for (dy in -1..1) {
                    if (field.row + dy in 0..7) {
                        if(dx != 0 || dy != 0) {
                            ret = ret or Field(f + dx + dy * 8)
                        }
                    }
                }
            }
        }
        ret
    }

    fun line(line: Int) = LINE[line]

    fun row(row: Int) = ROW[row]

    fun ray(field: Field, ray: Ray) = RAY[field.value][ray.ordinal]

    fun pawnAttack(field: Field, color: FigureColor) = PAWN_ATTACK[field.value][color.ordinal]

    fun knightAttack(field: Field) = KNIGHT_ATTACK[field.value]

    fun bishopAttack(field: Field) = BISHOP_ATTACK[field.value]

    fun rookAttack(field: Field) = ROOK_ATTACK[field.value]

    fun queenAttack(field: Field) = QUEEN_ATTACK[field.value]

    fun kingAttack(field: Field) = KING_ATTACK[field.value]
}