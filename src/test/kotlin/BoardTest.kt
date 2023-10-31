
import model.BOARD_SIZE
import model.Board
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
        TODO()
    }

    @Test
    fun `test legal moves`(){
        TODO()
    }

    fun `test pass move`(){
        TODO()
    }

    @Test
    fun `test invalid positions`(){
        TODO()
    }

}