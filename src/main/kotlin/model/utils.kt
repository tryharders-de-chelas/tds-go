package model

const val BOARD_SIZE = 9

val blackScore get() =
    when(BOARD_SIZE){
        9 -> -3.5
        13 -> -4.5
        19 -> -5.5
        else -> throw IllegalArgumentException("Invalid board size.")
    }

fun Board.seriesOfPlays(moves: List<String>): Board{
    var board = this
    for(move in moves){
        board = board.play(move).first
    }
    return board
}

