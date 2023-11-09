
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
    fun `test one legal move`(){
        org.junit.jupiter.api.assertDoesNotThrow {
            var board = Board()
                board = board.play("b2").first
                board.play("a1")

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
        val initialBoard = Board().seriesOfPlays(moves)
        val (board, capture) = initialBoard.play("g7")
        assertTrue(capture == 1)
        assertTrue(board["f7"] == State.FREE)
    }

    @Test
    fun `test ko rule`(){
        val moves = listOf("b1", "c1", "a2", "b2", "b3", "c3", "a4", "d2", "c2", )
        val initialBoard = Board().seriesOfPlays(moves)
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            initialBoard.play("b2")
        }
    }

    @Test
    fun `test territory count single`(){
        val moves = listOf("b1", "c1", "a2" )
        val Board = Board().seriesOfPlays(moves)
        assertTrue(Board.countTerritory()==1 to 0)
    }


    @Test
    fun `test territory count with capture`(){
        val moves = listOf("b1", "c1", "a2", "d2", "b3", "c3", "c2" ,"a9","a4")
        val Board = Board().seriesOfPlays(moves)
        assertTrue(Board.countTerritory()==3 to 0 )
    }
    @Test
    fun `test territory count`(){
        val moves = listOf("b1", "c1", "a2", "b2", "b3", "c3", "a4", "d2", "c2","a9" )
        val Board = Board().seriesOfPlays(moves)
        assertTrue(Board.countTerritory()==3 to 0 )
    }

    @Test
    fun `test territory full Board`(){
        val moves = listOf("b1","a1","a2")
        val Board = Board().seriesOfPlays(moves)
        assertTrue(Board.countTerritory()==79 to 0 )
    }

    @Test
    fun `test territory fifty fifty`(){
        val moves = listOf("d1","e1","d2","e2","d3","e3","d4","e4","d5","e5","d6","e6","d7","e7","d8","e8","d9","e9")
        val Board = Board().seriesOfPlays(moves)
        assertTrue(Board.countTerritory()==27 to 36 )
    }

}