package model

enum class Player{
    WHITE, BLACK;
    val other get() = if (this == WHITE) BLACK else WHITE
    val state get() = if(this == WHITE) State.WHITE else State.BLACK
}