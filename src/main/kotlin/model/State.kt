package model

enum class State(val value:String){
    FREE("."), WHITE("0"), BLACK("#");

    val other get() = if (this == BLACK) WHITE else if (this==WHITE) BLACK else FREE
}