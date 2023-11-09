package storage

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Game

object GameSerializer: Serializer<Game> {
    override fun serialize(data: Game): String = Json.encodeToString(data)

    override fun deserialize(text: String): Game = Json.decodeFromString<Game>(text)
}