package model

const val BOARD_SIZE = 9
var wantsToEnd=0

fun Board.seriesOfPlays(moves: List<String>): Board{
    var board = this
    for(move in moves){
        board = board.play(move).first
    }
    return board
}