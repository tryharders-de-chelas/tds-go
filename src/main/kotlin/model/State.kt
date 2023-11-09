package model

enum class State(val value:String){
    FREE("."), WHITE("#"), BLACK("0");

    val other get() = if (this == BLACK) WHITE else if (this==WHITE) BLACK else FREE
}