package pluto.chess

class ChessException: RuntimeException {

    constructor(message: String): super(message)

    constructor(message: String, reason: Throwable): super(message, reason)
}