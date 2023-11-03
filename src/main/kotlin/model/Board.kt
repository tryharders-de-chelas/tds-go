package model

data class Board(
    val board: List<Cell> = List(BOARD_SIZE * BOARD_SIZE){ Cell(it) },
    val turn: Int = 1,
    val pass: Pair<Int?, Int?> = null to null
) {
    private val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK

    operator fun get(str: String) = board[toPosition(str)].state

    private fun toPosition(str: String): Int {
        require((str.length == 2) || (str.length == 3 && str.indexOfFirst{ it.isLetter() } != 1)){"Invalid format"}
        val column: Int? = str.find{ it.isLetter() }?.lowercaseChar()?.code?.minus('a'.code)
        val row: Int? = str.filter{ it.isDigit()}.toIntOrNull()?.minus(1)
        requireNotNull(column){"Invalid column"}
        requireNotNull(row){"Invalid row"}
        require(isInBounds(row, column)){"Off limits"}
        return (row * BOARD_SIZE) + column
    }

    private fun isInBounds(row: Int, col: Int) = row in 0..<BOARD_SIZE && col in 0..<BOARD_SIZE

    private fun Cell.isFree() = state == State.FREE

    private fun Cell.canCapture(): List<Cell>{
        val cellsToCapture = mutableListOf<Cell>()
        for (cell in around())
            if(cell.state == player.other.state)
                cellsToCapture += cell.search(emptyList(), player.other.state, this)

        return cellsToCapture
    }

    private fun Cell.search(visited:List<Cell>, state:State, src: Cell = this): List<Cell> {
        if(this in visited)
            return emptyList()
        val list = mutableListOf<Cell>()
        for(cell in around()){
            if(cell == src)
                continue
            when(cell.state){
                state -> list += cell.search(visited + this, state, src)
                State.FREE -> return emptyList()
                else -> continue
            }
        }
        list += this
        return list
    }

    private fun Cell.up() = if(row == 1) null else board[id - BOARD_SIZE]

    private fun Cell.down() = if(row == BOARD_SIZE) null else board[id + BOARD_SIZE]

    private fun Cell.right() = if(col == BOARD_SIZE) null else board[id + 1]

    private fun Cell.left() = if(col == 1) null else board[id - 1]

    private fun Cell.around() = listOfNotNull(up(), down(), left(), right())

    private fun Cell.hasLiberty() = search(emptyList(), player.state).isEmpty()

    private fun capture(move: Cell, cellsToCapture: List<Cell>): List<Cell> =
        board.map{ cell ->
            when(cell) {
                move -> Cell(cell.id, player.state)
                in cellsToCapture -> Cell(move.id, State.FREE)
                else -> cell
            }
        }

    private fun switch(cell: Cell) = board.map { if(it == cell) cell.copy(state = player.state) else cell }

    private fun nextState(isCapture: Boolean, move: Cell, cellsToCapture: List<Cell>) =
        copy(
            board=if(isCapture) capture(move, cellsToCapture) else switch(move),
            turn=turn + 1
        )

    fun play(str: String): Pair<Board,Int> {
        val cell = board[toPosition(str)]
        require(cell.isFree()){"Illegal move"}
        val cellsToCapture = cell.canCapture()
        return when{
            cellsToCapture.isNotEmpty() -> nextState(true, cell, cellsToCapture) to cellsToCapture.size
            cell.hasLiberty() -> nextState(false, cell, cellsToCapture) to cellsToCapture.size
            else -> throw IllegalArgumentException("Illegal move")
        }
    }

    private fun show(): String{
        val letters = ((0..<BOARD_SIZE).map { 'A' + it }).joinToString( prefix = " ".repeat(2), separator = " ")
        val lines = board
            .chunked(BOARD_SIZE)
            .reversed()
            .joinToString(separator = "\n") {
                line -> line.joinToString(prefix = "${line.first().row} ", separator = " ") {
                    cell -> cell.state.value
                }
            }
        return (letters + "\n" + lines)
    }

    fun draw() = println(show())
}
