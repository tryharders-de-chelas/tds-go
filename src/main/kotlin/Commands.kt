package model
import kotlin.system.exitProcess
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter


fun receive(input:String){
    var splittedInput=input.split(" ")
    when(splittedInput[0]){
        "new" -> create()
        "play" -> move(splittedInput[1])
        "pass" -> {
            wantsToEnd++
            if (wantsToEnd==2) endGame()
            else move(null)
        }
        "save" -> saveBoard(splittedInput[1])
        "load" -> loadBoard(splittedInput[1])
        "exit" -> exitProcess(0)
        else -> {
            println("Invalid command $input")
            return
        }
    }
    if (splittedInput[0]!="pass") wantsToEnd=0

}


fun endGame(){
    //show board
    //GAME OVER and score

}
//pass 2 times draw
//confirm with stack
fun create(){

}

fun move(move:String?){

}

fun saveBoard(name:String){

}

fun loadBoard(name:String){
    val br=createReader(name)

    var line=br.readLine()
    while(line!=null){


        line=br.readLine()
    }

}

fun main(){
    while(true){
        receive(readln())
    }

}
fun createReader(fileName: String): BufferedReader {
    return BufferedReader(FileReader(fileName))
}

fun createWriter(fileName: String?): PrintWriter {
    return PrintWriter(fileName)
}