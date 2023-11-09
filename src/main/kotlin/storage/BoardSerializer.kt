package storage

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.BOARD_SIZE
import model.Board
import model.Cell

object BoardSerializer: Serializer<Board> {
    override fun serialize(data: Board): String = Json.encodeToString(data)

    override fun deserialize(text: String): Board = Json.decodeFromString<Board>(text)
}