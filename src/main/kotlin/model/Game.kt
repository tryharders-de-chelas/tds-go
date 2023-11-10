package model

import kotlinx.serialization.Serializable
import storage.GameSerializer
import storage.TextFileStorage

@Serializable
data class Game(
    private val board: Board = Board(),
    private val captures: Pair<Int, Int> = 0 to 0
    ){

    private val isOver = board.pass == true to true
/*
    init{

        require(!isOver)
    }

 */
    fun move(move: String): Game {
        require(!isOver){"Game over"}
        val (board, c) = board.play(move)
        val pair = if(board.player == Player.WHITE) (c to 0) else (0 to c)
        return copy(board = board, captures = (captures plus pair))
    }

    private fun results()=(blackScore to 0.0) plusDouble  board.countTerritory() plusDouble  captures


    fun pass()=copy(board=board.pass())

    fun saveBoard(name:String) =
        TextFileStorage<String, Game>("games/", GameSerializer).create(name, this).also {
            println("Game saved successfully")
        }

    fun loadBoard(name:String): Game {
        val storage = TextFileStorage<String, Game>("games/", GameSerializer).read(name)
            storage ?: throw IllegalArgumentException("game does not exist")
        return storage
    }


    fun show(){
        board.draw()
        val turn = "Turn: ${board.player.state.value} (${board.player.name})"
        val captures = "Captures: ${State.BLACK.value}=${captures.first} - ${State.WHITE.value}=${captures.second}"
        return when(board.pass){
            (true to true)-> {
                val result=results()
                println("GAME OVER     Score: ${State.BLACK.value}=${result.first} - ${State.WHITE.value}=${result.second}")
            }
            (false to false)-> println("$turn    $captures")
        else-> println("Player ${board.player.other.state} passes.   $turn")
    }}
}