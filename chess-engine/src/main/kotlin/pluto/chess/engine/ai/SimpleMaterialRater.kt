package pluto.chess.engine.ai

import pluto.chess.board.Board
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType

class SimpleMaterialRater : BoardRater {

    override fun rate(board: Board): Int {
        val pawn = board.getFigureMask(FigureType.PAWN)
        val knight = board.getFigureMask(FigureType.KNIGHT)
        val bishop = board.getFigureMask(FigureType.BISHOP)
        val rook = board.getFigureMask(FigureType.ROOK)
        val queen = board.getFigureMask(FigureType.QUEEN)
        val white = board.getColorMask(FigureColor.WHITE)
        val black = board.getColorMask(FigureColor.BLACK)

        var ret = ((pawn and white).fieldCount() - (pawn and black).fieldCount()) * PAWN_VALUE
        ret += ((knight and white).fieldCount() - (knight and black).fieldCount()) * KNIGHT_VALUE
        ret += ((bishop and white).fieldCount() - (bishop and black).fieldCount()) * BISHOP_VALUE
        ret += ((rook and white).fieldCount() - (rook and black).fieldCount()) * ROOK_VALUE
        ret += ((queen and white).fieldCount() - (queen and black).fieldCount()) * QUEEN_VALUE
        return if (board.colorOnTurn == FigureColor.WHITE) ret else -ret
    }

    companion object {

        val PAWN_VALUE = 1
        val KNIGHT_VALUE = PAWN_VALUE * 3
        val BISHOP_VALUE = PAWN_VALUE * 3
        val ROOK_VALUE = PAWN_VALUE * 5
        val QUEEN_VALUE = PAWN_VALUE * 9
    }
}