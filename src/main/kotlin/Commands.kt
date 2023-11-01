import model.Board
import model.Game
import kotlin.system.exitProcess
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
fun Game.receive(input:String): Game{
    val splitInput=input.split(" ")
    return when(splitInput[0]){
        "new" -> create()
        "play" -> move(splitInput[1])
        "pass" -> pass()
        "save" -> saveBoard(splitInput[1])
        "load" -> loadBoard(splitInput[1])
        "exit" -> exitProcess(0)
        else -> throw IllegalArgumentException("Invalid command $input")
    }

}
fun create() = Game(board = Board())

fun Game.move( move:String) = copy(board = board.play(move))

fun Game.pass(): Game{
    return pass()
}

fun Game.saveBoard(name:String): Game{
    val writer = createWriter(name)
    return this
}

fun loadBoard(name:String): Game{
    val br=createReader(name)
    var line=br.readLine()
    while(line!=null){

        serialize(line)
        line=br.readLine()
    }

}

fun serialize(line: String){
    TODO()
}

fun deserialize(game: Game){
    TODO()
}

fun main(){
    var game = create()
    while(true){
        game = game.receive(readln())
        game.show()
    }

}
fun createReader(fileName: String): BufferedReader {
    return BufferedReader(FileReader(fileName))
}

fun createWriter(fileName: String): PrintWriter {
    return PrintWriter(fileName)
}