package model

import kotlinx.serialization.*

@Serializable
data class Board(
    val board: List<Cell> = List(BOARD_SIZE * BOARD_SIZE){ Cell(it) },
    private val turn: Int = 1,
    val pass: Pair<Boolean, Boolean> = false to false,
    private val prevBoard: List<Cell> = board,
) {

    val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK

    operator fun get(str: String) = board[toPosition(str)].state

    private fun toPosition(str: String): Int {
        require((str.length == 2) || (str.length == 3 && str.indexOfFirst{ it.isLetter() } != 1)){"Invalid format"}
        val column: Int? = str.find{ it.isLetter() }?.lowercaseChar()?.code?.minus('a'.code)
        val row: Int? = str.filter{ it.isDigit()}.toIntOrNull()?.minus(1)
        if(column != null && row != null && isInBounds(row, column))
            return (row * BOARD_SIZE) + column
        throw IllegalArgumentException("$str is an invalid move")
    }

    private fun isInBounds(row: Int, col: Int) = row in 0..<BOARD_SIZE && col in 0..<BOARD_SIZE

    private fun Cell.isFree() = state == State.FREE

    fun pass(): Board {
        val pass = pass or ((player == Player.BLACK) to (player == Player.WHITE))
        return copy(board=board ,pass = pass , turn=turn+1)
    }

    private fun Cell.canCapture(): Set<Cell>{

        val cellsToCapture = mutableSetOf<Cell>()
        for (cell in around())
            if(cell.state == player.other.state){
                cellsToCapture+=cell.search(emptySet(), player.other.state, this)
            }

        return cellsToCapture.toSet()
    }

    /**
     * For our algorithm of counting territory we opted for creating a list of all the free spaces that are adjacent
     * to one another and for each group we see if they are surrounded by only one state of pieces(Black or White)
     */
    fun countTerritory():Pair<Int,Int>{
        var blackTerritory = 0
        var whiteTerritory = 0
        val visited= listFree()
        for (space in visited){
            when(searchSpace(space)){
                State.BLACK-> blackTerritory += space.size
                State.WHITE-> whiteTerritory += space.size
                else -> continue
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
            if(!cell.isFree())
                continue
            val freeCells = visited.flatten()
            if(cell !in freeCells)
                visited+= listOf(cell.searchFree(freeCells))
        }
        return visited
    }

    /**
     * Here we mark territory that we were given by searching the cells adjacent. If there is only one type adjacent we
     * can label that territory as Black or White otherwise if we find out that there are both we only return a free
     * state to signal that this territory is considered neutral
     */
    private fun searchSpace(free:List<Cell>):State{
        var state = State.FREE
        for (cell in free){
            for (adj in cell.around()){
                when(adj.state){
                    State.FREE, state -> continue
                    else ->
                        if(state==State.FREE)
                            state=adj.state
                        else
                            return State.FREE
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
        for(cell in around())
            if(cell.isFree())
                list += cell.searchFree(visited+this+list)

        list += this
        return list.toList()
    }

    /**
     * In the search function we find out if a space of a type of adjacent cells(Black or White) are surrounded by enemy
     * pieces meaning if there aren't any liberties we can tell that the space is surrounded by the opponent and that
     * we would be captured. We do this by recursively searching the cells around and seeing if there are any space that
     * is free. In this function we added a src argument so that it can work both ways and see if we capture any pieces
     * by adding that source one as an opponent's piece. If we see that there are liberties(a free cell adjacent) we
     * only return an empty list to signal that it's safe.
     */
    private fun Cell.search(visited: Set<Cell>, state: State, src: Cell = this): Set<Cell> {
        val list = mutableSetOf<Cell>()
        for(cell in around()){
            if(cell == src || cell in visited)
                continue
            when(cell.state){
                state ->{
                    val newCell=cell.search(visited + this, state, src)
                    if (newCell.isEmpty())
                        return emptySet()
                    list += newCell
                }
                State.FREE -> return emptySet()
                else -> continue
            }
        }
        list += this
        return list.toSet()
    }

    private fun Cell.up() = if(row == 1) null else board[id - BOARD_SIZE]

    private fun Cell.down() = if(row == BOARD_SIZE) null else board[id + BOARD_SIZE]

    private fun Cell.right() = if(col == BOARD_SIZE) null else board[id + 1]

    private fun Cell.left() = if(col == 1) null else board[id - 1]

    private fun Cell.around() = listOfNotNull(up(), down(), left(), right())

    private fun Cell.hasLiberty() = search(emptySet(), player.state).isEmpty()

    /**
     * Changes current board by adding the last piece played and removing the pieces that are captured. It also confirms
     * if there isn't a break in the KO rule since it can only happen if there is a possible capture
     */
    private fun capture(move: Cell, cellsToCapture: Set<Cell>): List<Cell> {
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

    private fun nextState(isCapture: Boolean, move: Cell, cellsToCapture: Set<Cell>) =
        copy(
            board=if(isCapture) capture(move, cellsToCapture) else switch(move),
            turn=turn + 1,
            pass=false to false,
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

     fun show(): String{
        val lines = board.chunked(BOARD_SIZE)
        var letters = "  "
        repeat(BOARD_SIZE){
           letters += "${ 'A' + it } "
        }
        letters += "\n"
        for(i in lines.lastIndex downTo 0){
            letters += lines[i].joinToString(prefix = "${i+1} ", separator = " ", postfix = "\n"){ it.state.value }
        }
        return letters
    }
}
