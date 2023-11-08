package model

data class Game(val board: Board){
    fun isOver() = board.pass == true to true
    fun show(){
        board.draw()

    }

    fun export(){
        TODO()
    }
}