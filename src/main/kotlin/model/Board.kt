package model

data class Board(
    val board: List<Cell> = List(BOARD_SIZE * BOARD_SIZE){ Cell(it) },
    val turn: Int = 1,
    val pass: Pair<Boolean, Boolean> = false to false,
    val prevBoard: List<Cell> = board
) {
    val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK

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
            if(cell.state == player.other.state){
                cellsToCapture+=cell.search(emptyList(), player.other.state, this)
            }

        return cellsToCapture
    }

    fun countTerritory():Pair<Int,Int>{
        var blackTerritory=0
        var whiteTerritory=0
        val visited= listFree()
        for (space in visited){
            val current =searchSpace(space)
            when(current){
                State.BLACK->blackTerritory+=space.size
                State.WHITE->whiteTerritory+=space.size
                else ->continue
            }
        }
        return blackTerritory to whiteTerritory
    }

    private fun listFree():List<List<Cell>>{
        var visited= emptyList<List<Cell>>()
        for(cell in board){
            if(cell.state!=State.FREE) continue
            if(cell !in visited.flatten())
                visited+= listOf(cell.searchFree(visited.flatten()))
        }
        return visited
    }

    private fun searchSpace(free:List<Cell>):State{
        var state=State.FREE
        for (cell in free){
            for (adj in cell.around()){
                when(adj.state){
                    State.FREE -> continue
                    state -> continue
                    else -> if(state==State.FREE) state=adj.state
                        else return State.FREE
                }

            }
        }
        return state
    }
    private fun Cell.searchFree(visited:List<Cell>): List<Cell>{
        if(this in visited)
            return emptyList()
        val list = mutableListOf<Cell>()
        for(cell in around()){
            when(cell.state){
                State.FREE-> list+=cell.searchFree(visited+this+list)
                else -> continue
            }
        }
        list += this
        return list
    }


    private fun Cell.search(visited:List<Cell>, state:State, src: Cell = this): List<Cell> {
        val list = mutableListOf<Cell>()
        for(cell in around()){
            if(cell == src || cell in visited)
                continue
            when(cell.state){
                state ->{
                    val newCell=cell.search(visited + this, state, src)
                    if (newCell.isEmpty()) return emptyList()
                        else list += newCell
                }
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

    private fun capture(move: Cell, cellsToCapture: List<Cell>): List<Cell> {
        val nextBoard = board.map{ cell ->
            when(cell) {
                move -> Cell(cell.id, player.state)
                in cellsToCapture -> Cell(cell.id, State.FREE)
                else -> cell
            }
        }
        require(nextBoard.map{it.state} != prevBoard.map{it.state}){"KO Rule"}
        return nextBoard
    }

    private fun switch(cell: Cell) = board.map { if(it == cell) cell.copy(state = player.state) else it }

    private fun nextState(isCapture: Boolean, move: Cell, cellsToCapture: List<Cell>) =
        copy(
            board=if(isCapture) capture(move, cellsToCapture) else switch(move),
            turn=turn + 1,
            pass=(pass.first && (player != Player.BLACK)) to (pass.second && (player != Player.WHITE)),
            prevBoard=board
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
