
import model.BOARD_SIZE
import model.Board
import model.State
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


class BoardTest {
    private val board = Board()
    @Test
    fun `test empty board`(){
        assertTrue(board.lines == BOARD_SIZE)
    }

    @Test
    fun `test illegal out of board move`(){
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("z1")
        }
    }

    @Test
    fun `test legal moves`(){
        board.play("a1")
        board.play("b1")
        board.play("c1")
        assertTrue(board.getPosition("a1").state == State.BLACK)
        assertTrue(board.getPosition("b1").state == State.WHITE)
        assertTrue(board.getPosition("c1").state == State.BLACK)
    }

    fun `test pass move`(){
        TODO()
    }

    @Test
    fun `test invalid positions`(){
        board.play("a1")
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            board.play("a1")
        }
    }

}