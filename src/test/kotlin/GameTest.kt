import model.Game
import model.blackScore
import model.seriesOfMoves
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GameTest {

    @Test
    fun `Test score`(){
        val moves = listOf("a1", "a2", "d5", "b2", "c5", "b1")
        val game = Game().seriesOfMoves(moves)
        assertEquals(game.score(), (blackScore to 2.0))
    }

}