package model

const val BOARD_SIZE = 9

enum class State(val value:String){
    FREE("."), WHITE("#"), BLACK("0")
}
