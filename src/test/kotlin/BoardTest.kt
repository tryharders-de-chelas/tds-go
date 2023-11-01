
import model.BOARD_SIZE
import model.Board
import model.State
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class BoardTest {

    @Test
    fun `test illegal out of board move`(){
        val board = Board()
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("z1")
        }
    }

    @Test
    fun `test legal moves`(){
        val board = Board()
        board.play("a1")
        board.play("b1")
        board.play("c1")
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
    fun `test invalid position`(){
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
        var board = Board()
        board = board.play("a1").first
        board = board.play("a2").first
        board = board.play("d5").first
        board = board.play("b2").first
        board = board.play("c5").first
        board = board.play("b1").first
        board.draw()
        assertTrue(board["a1"] == State.FREE)
        assertTrue(board["a2"] == State.WHITE)
        assertTrue(board["b2"] == State.WHITE)
        assertTrue(board["b1"] == State.WHITE)
        assertTrue(board["d5"] == State.BLACK)
        assertTrue(board["c5"] == State.BLACK)
    }

    @Test
    fun `test capture in center`(){
        var board = Board()
        board = board.play("f7").first
        board = board.play("f6").first
        board = board.play("a1").first
        board = board.play("f8").first
        board = board.play("a8").first
        board = board.play("e7").first
        board = board.play("b1").first
        val move = board.play("g7")
        board = move.first
        val capture = move.second
        board.draw()
        assertTrue(capture == 1)
        assertTrue(board["f7"] == State.FREE)
    }
}