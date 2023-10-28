fun receive(input:String){
    var splittedInput=input.split(" ")
    when(splittedInput[0]){
        "new" -> create()
        "play" -> move(splittedInput[1])
        "pass" -> move(null)
        "save" -> saveBoard()
        "load" -> loadBoard()
        "exit" -> exitProcess()
        else -> println("Invalid command $input")
    }

}
