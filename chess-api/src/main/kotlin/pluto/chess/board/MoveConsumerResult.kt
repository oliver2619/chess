package pluto.chess.board

enum class MoveConsumerResult {
    CANCEL, CONTINUE_VALUE_CHANGING, CONTINUE_ALL;

    infix fun combine(other: MoveConsumerResult): MoveConsumerResult = if (this.ordinal < other.ordinal) this else other
}