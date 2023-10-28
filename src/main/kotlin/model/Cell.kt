package model

class Cell(val state: State = State.FREE) {

}

enum class State(val value:String){
    FREE("."), WHITE("#"), BLACK("0")
}