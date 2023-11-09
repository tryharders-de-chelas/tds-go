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


    /**
     * For our algorithm of the captures we opted for an option where we reuse the algorithm where we see if a group of
     * pieces is captured ,(A.K.A. ou Cell.search function) by seeing if the pieces around the cell we are putting on the
     * board are captured by our own pieces, we do this by setting the state of our search function to the opponents state
     */
    private fun Cell.canCapture(): List<Cell>{
        val cellsToCapture = mutableListOf<Cell>()
        for (cell in around())
            if(cell.state == player.other.state){
                cellsToCapture+=cell.search(emptyList(), player.other.state, this)
            }

        return cellsToCapture
    }

    /**
     * For our algorithm of counting territory we opted for creating a list of all the free spaces that are adjacent
     * to one another and for each group we see if they are surrounded by only one state of pieces(Black or White)
     */
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

    /**
     * To create a list of all the free spaces we run through the whole board where we only add the ones
     * where we have not yet visited
     */
    private fun listFree():List<List<Cell>>{
        var visited= emptyList<List<Cell>>()
        for(cell in board){
            if(!cell.isFree()) continue
            if(cell !in visited.flatten())
                visited+= listOf(cell.searchFree(visited.flatten()))
        }
        return visited
    }

    /**
     * Here we mark territory that we were given by searching the cells adjacent. If there is only one type adjacent we
     * can label that territory as Black or White otherwise if we find out that there are both we only return a free
     * state to signal that this territory is considered neutral
     */
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

    /**
     * In this function we gather all the adjacent cells that are free by searching the cells that are around recursively
     * without making any loops by keeping a track of the ones where we've passed through. We do this so that we
     * can create a list of the "territory" that we are going to mark later on
     */
    private fun Cell.searchFree(visited:List<Cell>): List<Cell>{
        if(this in visited)
            return emptyList()
        val list = mutableListOf<Cell>()
        for(cell in around()){
            if(cell.isFree()) list+=cell.searchFree(visited+this+list)
        }
        list += this
        return list
    }

    /**
     * In the search function we find out if a space of a type of adjacent cells(Black or White) are surrounded by enemy
     * pieces meaning if there aren't any liberties we can tell that the space is surrounded by the opponent and that
     * we would be captured. We do this by recursively searching the cells around and seeing if there are any space that
     * is free. In this function we added a src argument so that it can work both ways and see if we capture any pieces
     * by adding that source one as an opponent's piece. If we see that there are liberties(a free cell adjacent) we
     * only return an empty list to signal that it's safe.
     */
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

    /**
     * Changes current board by adding the last piece played and removing the pieces that are captured. It also confirms
     * if there isn't a break in the KO rule since it can only happen if there is a possible capture
     */
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

    /**
     * Creates the new Board with the new position either from capture or switch and updates turns, the pass the same
     * player in his last turn passed and the previous Board so that we can confirm a possible break in the K0 rule
     */
    private fun nextState(isCapture: Boolean, move: Cell, cellsToCapture: List<Cell>) =
        copy(
            board=if(isCapture) capture(move, cellsToCapture) else switch(move),
            turn=turn + 1,
            pass=(pass.first && (player != Player.BLACK)) to (pass.second && (player != Player.WHITE)),
            prevBoard=board
        )

    /**
     * This function receives the coordinates that the player chose and confirms if the move is legal, if it is it
     * returns the updated board with the number of cells captured. We do this by confirming first if the move is inside
     * the table and there isn't any piece in the same cell, then it confirms if we capture any piece since if we capture
     * it's a legal move, then if we cant capture any piece we confirm if it isn't a suicidal move and only then we say
     * that we really cant play that move.
     */
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
