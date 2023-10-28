import kotlin.system.exitProcess

fun receive(input:String){
    var splittedInput=input.split(" ")
    when(splittedInput[0]){
        "new" -> create()
        "play" -> move(splittedInput[1])
        "pass" -> move(null)
        "save" -> saveBoard(splittedInput[1])
        "load" -> loadBoard(splittedInput[1])
        "exit" -> exitProcess(0)
        else -> println("Invalid command $input")
    }

}
