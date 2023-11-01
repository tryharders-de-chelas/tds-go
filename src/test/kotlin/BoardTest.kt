
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
    fun `test invalid positions`(){
        val board = Board()
        board.play("a1")
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("a1")
        }
    }

    @Test
    fun `test capture`(){
        val board = Board()
        board.play("a1")
        board.play("a2")
        board.play("d5")
        board.play("b2")
        board.play("c5")
        board.play("b1")
        assertTrue(board["a1"] == State.FREE)
        assertTrue(board["a2"] == State.WHITE)
        assertTrue(board["b2"] == State.WHITE)
        assertTrue(board["b1"] == State.WHITE)
        assertTrue(board["d5"] == State.BLACK)
        assertTrue(board["c5"] == State.BLACK)
    }
}