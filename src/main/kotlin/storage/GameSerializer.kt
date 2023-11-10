package storage

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Game

object GameSerializer: Serializer<Game> {
    override fun serialize(data: Game): String = Json{
        prettyPrint=true
        prettyPrintIndent=" "
        encodeDefaults=true
    }.encodeToString(data)

    override fun deserialize(text: String): Game = Json.decodeFromString<Game>(text)
}