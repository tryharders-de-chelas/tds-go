package model

data class Cell(
    val id: Int,
    val state: State = State.FREE,
    val visited: Boolean = false,
){
    val row get() = (id / BOARD_SIZE) + 1
    val col get() = (id % BOARD_SIZE) + 1
}