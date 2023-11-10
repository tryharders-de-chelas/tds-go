import model.Game
import kotlin.system.exitProcess
fun Game.receive(input:String): Game{
    val splitInput=input.split(" ")
    return when(splitInput[0]){
        "new" -> Game()
        "play" -> move(splitInput[1])
        "pass" -> pass()
        "save" -> also { saveBoard(splitInput[1]) }
        "load" -> loadBoard(splitInput[1])
        "exit" -> exitProcess(0)
        else -> throw IllegalArgumentException("Invalid command $input")
    }
}


fun main(){
    var game = Game()
    game.show()
    while(true){
        game = game.receive(readln())
        game.show()
    }

}