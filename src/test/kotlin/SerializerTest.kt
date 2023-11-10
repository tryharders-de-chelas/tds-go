import model.Game
import model.seriesOfMoves
import org.junit.jupiter.api.Test
import storage.GameSerializer
import kotlin.test.assertEquals

class SerializerTest {
    @Test
    fun `test serializer`(){
        val moves = listOf("a1", "a2", "d5", "b2", "c5", "b1")
        val game = Game().seriesOfMoves(moves)
        val json = GameSerializer.serialize(game)
        assertEquals(game, GameSerializer.deserialize(json))
    }
}