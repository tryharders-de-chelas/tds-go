package storage

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.Game

object GameSerializer: Serializer<Game> {
    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = " "
        encodeDefaults = true
    }

    override fun serialize(data: Game): String = json.encodeToString(data)

    override fun deserialize(text: String): Game = Json.decodeFromString<Game>(text)
}