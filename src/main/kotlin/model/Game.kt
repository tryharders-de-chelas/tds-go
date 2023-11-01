package model

data class Game(val board: Board){
    fun show(){
        board.draw()

    }
}