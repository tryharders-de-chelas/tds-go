import model.Game

fun main(){
    var game = Game()
    do {
        try {
            game.show()
            print(">")
            game = game.execute(readln())
        } catch (e:Exception) {
            println(e.message)
        }
    } while (true)
}