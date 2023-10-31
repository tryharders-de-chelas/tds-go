package model

class Board {
    private val board = Array(BOARD_SIZE) { Array(BOARD_SIZE){ Cell() } }
    val lines = board.size
    private var turn = 1
    private val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK

    private fun isInBounds(position: Pair<Int, Int>) = position.first in 0..BOARD_SIZE && position.second in 0..BOARD_SIZE
    fun draw(){
        println(((0..<lines).map { 'A' + it }).joinToString( prefix = " ".repeat(2), separator = " "))
        board.forEachIndexed { idx, line -> println(line.joinToString(prefix = "${lines - idx} ", separator = " ") { it.state.value }) }
    }

    fun play(str: String){
        val position = isPosition(str)
        position ?: return
        if(isLegal(position)){
            board[position.first][position.second] = Cell(player.state)
            turn++
        }
    }

    private fun isPosition(str: String): Pair<Int, Int> {
        require(str.length !in 2..3 && str.indexOfFirst{ it.isLetter() } == 1){"Invalid format"}
        val column: Int? = str.find{ it.isLetter() }?.lowercase()?.toInt()?.minus('a'.code)
        val line: Int? = str.takeWhile{ it.isDigit() }.toIntOrNull()
        requireNotNull(column){"Invalid column"}
        requireNotNull(line){"Invalid line"}
        return line to column
    }

    private fun isLegal(position: Pair<Int, Int>) = isFree(position) && isInBounds(position) && cantBeCaptured(position)


    private fun isFree(position: Pair<Int, Int>) = board[position.first][position.second].state == State.FREE

    private fun cantBeCaptured(position: Pair<Int, Int>) = surroundingCells(position).all { it.state == player.other.state } //Incomplete

    private fun surroundingCells(position: Pair<Int, Int>): MutableList<Cell>{
        val mutableList = mutableListOf<Cell>()
        if(position.first != 0)
            mutableList.add(board[position.first-1][position.second])
        if(position.first != BOARD_SIZE)
            mutableList.add(board[position.first+1][position.second])
        if(position.second != 0)
            mutableList.add(board[position.first][position.second-1])
        if(position.second != BOARD_SIZE)
            mutableList.add(board[position.first][position.second+1])
        return mutableList
    }
}
