package model

import blackScore
import kotlinx.serialization.Serializable
import plus
import plusDouble
import storage.GameSerializer
import storage.JsonFileStorage
import kotlin.system.exitProcess

@Serializable
data class Game(
    private val board: Board = Board(),
    private val captures: Pair<Int, Int> = 0 to 0
    ){

    private val isOver = board.pass == true to true

    fun execute(command:String): Game {
        val splitInput=command.split(" ")
        return when(splitInput[0]){
            "new" -> Game()
            "play" -> move(splitInput[1])
            "pass" -> pass()
            "save" -> also { saveBoard(splitInput[1]) }
            "load" -> loadBoard(splitInput[1])
            "exit" -> exitProcess(0)
            else -> throw IllegalArgumentException("Invalid command $command")
        }
    }

    fun move(move: String): Game {
        require(!isOver){"Game over"}
        val (board, c) = board.play(move)
        val pair = if(board.player == Player.WHITE) (c to 0) else (0 to c)
        return copy(board = board, captures = (captures plus pair))
    }

    internal fun score()=(blackScore to 0.0) plusDouble  board.countTerritory() plusDouble  captures

    private fun pass()=copy(board=board.pass())

    private fun saveBoard(name:String) =
        JsonFileStorage<String, Game>("games/", GameSerializer).create(name, this).also {
            println("Game saved successfully")
        }

    private fun loadBoard(name: String): Game =
        JsonFileStorage<String, Game>("games/", GameSerializer).read(name)


    fun show(){
        val results=score()
        val turn = "Turn: ${board.player.state.value} (${board.player.name})"
        val captures = "Captures: ${State.BLACK.value}=${captures.first} - ${State.WHITE.value}=${captures.second}"
        val score = "Score: ${State.BLACK.value}=${results.first} - ${State.WHITE.value}=${results.second}"
        println(board.show())
        return when(board.pass){
            true to true -> println("GAME OVER     $score")
            false to false -> println("$turn    $captures")
            else -> println("Player ${board.player.other.state} passes.   $turn")
        }
    }
}