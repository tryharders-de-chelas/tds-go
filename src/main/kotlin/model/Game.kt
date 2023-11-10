package model

import kotlinx.serialization.Serializable
import storage.GameSerializer
import storage.TextFileStorage

@Serializable
data class Game(
    private val board: Board = Board(),
    private val captures: Pair<Int, Int> = 0 to 0
    ){

    private val isOver get() = board.pass == true to true

    fun move(move: String): Game {
        val (board, c) = board.play(move)
        val pair = if(board.player == Player.WHITE) (0 to c) else (c to 0)
        return copy(board = board, captures = (captures plus pair))
    }

    fun results()=(blackScore to 0.0) plus board.countTerritory() plus captures


    fun pass(): Game {
        val pass = board.pass or ((board.player == Player.BLACK) to (board.player == Player.WHITE))
        return copy(board=board.copy(pass = pass))
    }

    fun saveBoard(name:String) =
        TextFileStorage<String, Game>("games/", GameSerializer).create(name, this).also {
            println("Game saved successfully")
        }

    fun loadBoard(name:String): Game {
        val storage = TextFileStorage<String, Game>("games/", GameSerializer)
        return storage.read(name) ?: throw IllegalArgumentException("game does not exist")
    }


    fun show(score: Pair<Double, Double>? = null){
        board.draw()
        if(score is Pair<Double, Double>)
            return println("Game Over $score")

        val turn = "Turn: ${board.player.state} (${board.player.name})"
        val captures = "Captures: ${State.BLACK}=${captures.first} - ${State.WHITE}=${captures.second}"

        if(board.pass == (false to false))
            return println("$turn    $captures")
        return println("Player ${board.player.state} passes.   $turn")
    }
}