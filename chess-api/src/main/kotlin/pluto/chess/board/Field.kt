package pluto.chess.board

import pluto.chess.ChessException

class Line internal constructor(private val line: Int) {
    val _1 get() = Field(line)
    val _2 get() = Field(line or 8)
    val _3 get() = Field(line or 16)
    val _4 get() = Field(line or 24)
    val _5 get() = Field(line or 32)
    val _6 get() = Field(line or 40)
    val _7 get() = Field(line or 48)
    val _8 get() = Field(line or 56)
}

@JvmInline
value class Field internal constructor(val value: Int) {

    val line get() = value and 7

    val row get() = value shr 3

    fun toMask() = FIELDS[value]

    internal infix fun manhattan(other: Field): Int = Math.abs(this.row - other.row) + Math.abs(this.line - other.line)

    infix fun or(other: Field): Mask = toMask() or other.toMask()

    infix fun shiftLine(amount: Int): Field {
        val newLine = line + amount
        if (newLine !in 0..7) {
            throw ChessException("Illegal field position")
        }
        return Field(value + amount)
    }

    infix fun shiftRow(amount: Int): Field {
        val newRow = row + amount
        if (newRow !in 0..7) {
            throw ChessException("Illegal field position")
        }
        return Field(value + amount * 8)
    }

    override fun toString(): String {
        return "${'a' + line}${'1' + row}"
    }

    companion object {

        private val FIELDS: Array<Mask> = Array(64) { field -> Mask(1UL shl field) }

        val A = Line(0)
        val B = Line(1)
        val C = Line(2)
        val D = Line(3)
        val E = Line(4)
        val F = Line(5)
        val G = Line(6)
        val H = Line(7)

        fun atIndex(index: Int): Field {
            if (index !in 0..63) {
                throw ChessException("Field index must be in range of 0..63")
            }
            return Field(index)
        }
    }
}