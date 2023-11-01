package model

data class Game(val board: Board)

fun Game.play(move: String) = copy(board = board.play(move))

fun Game.export(){
    TODO()
}

fun Game.import(){
    TODO()
}