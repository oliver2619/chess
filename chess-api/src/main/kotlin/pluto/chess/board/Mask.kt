package pluto.chess.board

import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate
import java.util.stream.Stream

@JvmInline
value class Mask internal constructor(val value: ULong = 0UL) {

    infix fun and(other: Mask) = Mask(value and other.value)
    infix fun or(other: Mask) = Mask(value or other.value)
    infix fun or(field: Field) = Mask(value or field.toMask().value)

    fun inv() = Mask(value.inv())

    fun fieldCount(): Int {
        var ret = 0
        val invMask = value.inv()
        var m = value and (invMask + 1UL)
        while (m != 0UL) {
            ++ret
            m = value and (invMask + (m shl 1))
        }
        return ret
    }

    fun isEmpty(): Boolean = value == 0UL

    fun isNotEmpty(): Boolean = value != 0UL

    fun lowestBit(): Mask = Mask(value and (value.inv() + 1UL))

    fun someFields(predicate: Predicate<Field>): Boolean {
        val invMask = value.inv()
        var m = value and (invMask + 1UL)
        while (m != 0UL) {
            if (predicate.test(Mask(m).toField())) {
                return true
            }
            m = value and (invMask + (m shl 1))
        }
        return false
    }

    fun everyField(predicate: Predicate<Field>): Boolean {
        val invMask = value.inv()
        var m = value and (invMask + 1UL)
        while (m != 0UL) {
            if (!predicate.test(Mask(m).toField())) {
                return false
            }
            m = value and (invMask + (m shl 1))
        }
        return true
    }

    fun forEachField(consumer: Function<in Field, out MoveConsumerResult>): MoveConsumerResult {
        val invMask = value.inv()
        var m = value and (invMask + 1UL)
        var ret = MoveConsumerResult.CONTINUE_ALL
        while (m != 0UL) {
            ret = ret combine consumer.apply(Mask(m).toField())
            if (ret == MoveConsumerResult.CANCEL) {
                return ret
            }
            m = value and (invMask + (m shl 1))
        }
        return ret
    }

    fun streamFields(): Stream<Field> {
        val invMask = value.inv()
        return Stream.iterate(value and (invMask + 1UL), { it != 0UL }, { value and (invMask + (it shl 1)) })
            .map { Mask(it).toField() };
    }

    fun reduceFieldsToInt(startValue: Int, reducer: BiFunction<Int, Field, Int>): Int {
        val invMask = value.inv()
        var m = value and (invMask + 1UL)
        var ret = startValue
        while (m != 0UL) {
            ret = reducer.apply(ret, Mask(m).toField())
            m = value and (invMask + (m shl 1))
        }
        return ret
    }

    fun toField(): Field = Field((Math.log(value.toDouble()) / Math.log(2.0)).toInt())
}