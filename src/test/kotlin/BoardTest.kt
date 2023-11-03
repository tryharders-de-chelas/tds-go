
import model.Board
import model.State
import model.seriesOfPlays
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class BoardTest {

    @Test
    fun `test illegal out of board move`(){
        val board = Board()
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("x1")
        }
    }

    @Test
    fun `test legal moves`(){
        val moves = listOf("a1", "b1", "c1")
        val board = Board().seriesOfPlays(moves)
        assertTrue(board["a1"] == State.BLACK)
        assertTrue(board["b1"] == State.WHITE)
        assertTrue(board["c1"] == State.BLACK)
    }

    @Test
    fun `test invalid positions on board`(){
        var board = Board()
        board = board.play("a1").first
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("a1")
        }
    }


    @Test
    fun `test positions`(){
        var board = Board()
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board = board.play("aaa2").first
        }
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board = board.play("a256").first
        }
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board = board.play("2ab").first
        }
        org.junit.jupiter.api.assertDoesNotThrow {
            board = board.play("2a").first
        }
        org.junit.jupiter.api.assertDoesNotThrow {
            board = board.play("b2").first
        }
    }

    @Test
    fun `test capture in corner`(){
        val moves = listOf("a1", "a2", "d5", "b2", "c5", "b1")
        val board = Board().seriesOfPlays(moves)
        assertTrue(board["a1"] == State.FREE)
        assertTrue(
            listOf(board["a2"],board["b2"], board["b1"]).all{it == State.WHITE}
        )
        assertTrue(
            listOf(board["d5"], board["c5"]).all{it == State.BLACK}
        )
    }

    @Test
    fun `test capture one stone in center`(){
        val moves = listOf("f7", "f6", "a1", "f8", "a8", "e7", "b1")
        var board = Board().seriesOfPlays(moves)
        val move = board.play("g7")
        board = move.first
        val capture = move.second
        assertTrue(capture == 1)
        assertTrue(board["f7"] == State.FREE)
    }

}