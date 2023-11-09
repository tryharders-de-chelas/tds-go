import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Board
import model.seriesOfPlays
import org.junit.jupiter.api.Test
import storage.BoardSerializer

class SerializerTest {

    @Test
    fun `test serializer`(){
        val moves = listOf("a1", "d5", "b2")
        val board = Board().seriesOfPlays(moves)
        println(BoardSerializer.serialize(board))
    }
}