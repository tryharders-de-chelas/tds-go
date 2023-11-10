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
    for(move in moves)
        board = board.play(move).first
    return board
}

fun Game.seriesOfMoves(moves: List<String>): Game{
    var game = this
    for(move in moves)
        game = game.move(move)
    return game
}

infix fun Pair<Boolean, Boolean>.or(other: Pair<Boolean, Boolean>) = (first || other.first) to (second || other.second)

infix fun Pair<Boolean, Boolean>.and(other: Pair<Boolean, Boolean>) = (first && other.first) to (second && other.second)

infix fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = (first + other.first) to (second + other.second)

infix fun Pair<Double, Double>.plus(other: Pair<Int, Int>) = (first + other.first) to (second + other.second)

