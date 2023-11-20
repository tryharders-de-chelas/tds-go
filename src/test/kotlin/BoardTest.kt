
import model.Board
import model.State
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
                board.play("a1").first
        }
    }

    @Test
    fun `test legal moves`(){
        val moves = listOf("a1", "b1", "c1")
        val board = Board().seriesOfPlays(moves)
        assertEquals(State.BLACK, board["a1"])
        assertEquals(State.WHITE, board["b1"])
        assertEquals(State.BLACK, board["c1"])
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
        assertEquals(State.FREE, board["a1"])
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
        assertEquals(1, capture)
        assertEquals(State.FREE, board["f7"])
    }

    @Test
    fun `test suicidal move one cell`() {
        val moves = listOf("a8", "a1", "b9")
        val initialBoard = Board().seriesOfPlays(moves)
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            initialBoard.play("a9")
        }
    }

    @Test
    fun `test suicidal move`() {
        val moves = listOf(
            "c3", "c2", "d3", "d2", "e3", "e2", "c4", "b3", "d4", "b4", "i1", "c5",
            "i2", "d5", "i3", "f3", "i4", "f4", "i5", "e5"
        )
        val initialBoard = Board().seriesOfPlays(moves)
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            initialBoard.play("e4")
        }
    }

    @Test
    fun `test capture surrounded and territory`() {
        val moves = listOf("d4","c4","d5","c5","e5","d6","f5","e6","f4","f6","f3","g5","e3","g4",
            "d3","g3","a1","f2","a2","e2","a3","d2","a4","c3","a5")
        val initialBoard = Board().seriesOfPlays(moves)
        val (board,capture)=initialBoard.play("e4")
        assertEquals(8, capture)
        assertEquals(0 to 8, board.countTerritory())
    }

    @Test
    fun `test ko rule`(){
        val moves = listOf("b1", "c1", "a2", "b2", "b3", "c3", "a4", "d2", "c2")
        val initialBoard = Board().seriesOfPlays(moves)
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            initialBoard.play("b2")
        }
    }

    @Test
    fun `test ko rule with a 2 piece capture`(){
        val moves = listOf("b2","e1","c1","f2","d1","e3","e2","c2","c3","i2","d3")
        val initialBoard = Board().seriesOfPlays(moves)
        val (board1, captures1)=initialBoard.play("d2")
        assertEquals(captures1, 1)
        val (board2, captures2)=board1.play("e2")
        assertEquals(captures2, 2)
        val (_, captures3)=board2.play("d2")
        assertEquals(captures3, 1)
    }

    @Test
    fun `test territory count single`(){
        val moves = listOf("b1", "c1", "a2" )
        val board = Board().seriesOfPlays(moves)
        assertEquals(1 to 0, board.countTerritory())
    }

    @Test
    fun `test territory count`(){
        val moves = listOf("b1", "c1", "a2", "d2", "b3", "c3", "c2" ,"a9","a4")
        val board = Board().seriesOfPlays(moves)
        assertEquals(3 to 0, board.countTerritory() )
    }

    @Test
    fun `test territory count  with capture`(){
        val moves = listOf("b1", "c1", "a2", "b2", "b3", "c3", "a4", "d2", "c2","a9" )
        val board = Board().seriesOfPlays(moves)
        assertEquals(3 to 0, board.countTerritory())
    }

    @Test
    fun `test territory full Board`(){
        val moves = listOf("b1","a1","a2")
        val board = Board().seriesOfPlays(moves)
        assertEquals(79 to 0, board.countTerritory())
    }

    @Test
    fun `test territory fifty fifty`(){
        val moves = listOf("d1","e1","d2","e2","d3","e3","d4","e4","d5","e5","d6","e6","d7","e7","d8","e8","d9","e9")
        val board = Board().seriesOfPlays(moves)
        assertEquals(27 to 36, board.countTerritory())
    }

    @Test
    fun `test territory with neutral`(){
        val moves = listOf("d1","f1","d2","f2","d3","f3","d4","f4","d5","f5","d6","f6","d7","f7","d8","f8","d9","f9")
        val board = Board().seriesOfPlays(moves)
        assertEquals(27 to 27, board.countTerritory())
    }

    @Test
    fun `test simultaneous captures`(){
        val moves = listOf("d4", "d5", "e5", "e6", "f6", "d7", "e7", "c6", "d8", "a1", "c7", "a2", "b6", "a3", "c5", "a4")
        val board = Board().seriesOfPlays(moves)
        val (_, captures) = board.play("d6")
        assertEquals(4, captures)
    }
}
