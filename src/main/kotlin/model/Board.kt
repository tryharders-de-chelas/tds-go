package model

class Board {
    val board = Array(BOARD_SIZE) { Array(BOARD_SIZE){ Cell() } }
    val lines = board.size

}