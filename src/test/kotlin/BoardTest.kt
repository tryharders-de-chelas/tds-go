
import model.BOARD_SIZE
import model.Board
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardTest {
    @Test
    fun `test empty board`(){
        val board = Board()
        assertTrue(board.lines == BOARD_SIZE)
    }

}