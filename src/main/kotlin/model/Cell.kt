package model

import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    val id: Int,
    val state: State = State.FREE,
){
    override fun equals(other: Any?): Boolean {
        if(other is Cell)
            return id == other.id
        return false
    }

    override fun hashCode(): Int = id

    val row get() = (id / BOARD_SIZE) + 1
    val col get() = (id % BOARD_SIZE) + 1
}