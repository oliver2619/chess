package pluto.chess.notation.pgn

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pluto.chess.ChessAssert
import pluto.chess.board.Board
import pluto.chess.board.BoardEditor
import pluto.chess.board.Field
import pluto.chess.board.GameResult
import pluto.chess.figure.FigureColor
import pluto.chess.figure.FigureType
import java.io.StringWriter
import java.lang.RuntimeException

internal class PgnWriterTest {
    @Test
    internal fun testFromStart() {
        // given
        val writer = StringWriter()
        val metaData = PgnMetaDataBuilder().white("w")
            .black("b")
            .event("Test")
            .site("localhost")
            .data("Custom", "Value")
            .result(GameResult.UNDEFINED)
            .build()
        val pgnWriter = PgnWriter(metaData, writer)
        val moveWriter = pgnWriter.moves()
        var move = Board.newGame().findMove(Field.E._2, Field.E._4)?:throw RuntimeException()
        moveWriter.move(move.move)
        move = move.board.findMove(Field.E._7, Field.E._5)?:throw RuntimeException()
        moveWriter.move(move.move)
        move = move.board.findMove(Field.G._1, Field.F._3)?:throw RuntimeException()
        moveWriter.move(move.move)
        moveWriter.end()
        // when
        val result = writer.toString()
        // then
        assertEquals("[Event \"Test\"]\n[Site \"localhost\"]\n[Date \"??\"]\n[Round \"1\"]\n[White \"w\"]\n[Black \"b\"]\n[Result \"*\"]\n[Custom \"Value\"]\n1. e4 e5 2. Nf3 *", result)
    }

    @Test
    internal fun testFromFen() {
        // given
        val writer = StringWriter()
        val metaData = PgnMetaDataBuilder().white("w")
            .black("b")
            .event("Test")
            .site("localhost")
            .data("Custom", "Value")
            .result(GameResult.WHITE_WON)
            .build()
        val pgnWriter = PgnWriter(metaData, writer)
        val board = BoardEditor.withKings(whiteKing = Field.E._6)
            .insertFigure(FigureColor.WHITE, FigureType.ROOK, Field.A._7)
            .resume(FigureColor.WHITE)
        val moveWriter = pgnWriter.setBoard(board, 100).moves()
        var move = board.findMove(Field.A._7, Field.A._8)?:throw RuntimeException()
        moveWriter.move(move.move)
        moveWriter.end()
        // when
        val result = writer.toString()
        // then
        assertEquals("[Event \"Test\"]\n[Site \"localhost\"]\n[Date \"??\"]\n[Round \"1\"]\n[White \"w\"]\n[Black \"b\"]\n[Result \"1-0\"]\n[SetUp \"1\"]\n[FEN \"4k3/R7/4K3/8/8/8/8/8 w - - 0 51\"]\n[Custom \"Value\"]\n51. Ra8# 1-0", result)
    }
}