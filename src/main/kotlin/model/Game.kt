package model

import kotlinx.serialization.Serializable

@Serializable
data class Game(val board: Board, val score: Pair<Double, Double> = blackScore to .0){
    fun isOver() = board.pass == true to true
    fun show(){
        board.draw()

    }
}