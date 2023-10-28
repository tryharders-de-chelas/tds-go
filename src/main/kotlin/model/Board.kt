package model

class Board {
    private val board = Array(BOARD_SIZE) { Array(BOARD_SIZE){ Cell() } }
    val lines = board.size
    fun draw(){
        println(((0..<lines).map { 'A' + it }).joinToString( prefix = " ".repeat(2), separator = " "))
        board.forEachIndexed { idx, line -> println(line.joinToString(prefix = "${lines - idx} ", separator = " ") { it.state.value }) }
    }

}