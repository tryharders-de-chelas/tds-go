package model

data class Board(
    val board: List<Cell> = List(BOARD_SIZE * BOARD_SIZE){ Cell(((BOARD_SIZE * BOARD_SIZE) - 1) - it) },
    val turn: Int = 1,
    val pass: Pair<Int?, Int?> = null to null
) {
    private val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK
    operator fun get(str: String) = board[toPosition(str)].state

    private fun toPosition(str: String): Int {
        require(
            (str.length == 2) || (str.length == 3 && str.indexOfFirst{ it.isLetter() } != 1)
        ){"Invalid format"}
        val column: Int? = str.find{ it.isLetter() }?.lowercaseChar()?.code?.minus('a'.code)
        val row: Int? = str.filter{ it.isDigit()}.toIntOrNull()?.minus(1)
        requireNotNull(column){"Invalid column"}
        requireNotNull(row){"Invalid row"}
        require(isInBounds(row, column)){"Off limits"}
        return (row * BOARD_SIZE) + column
    }

    private fun isInBounds(row: Int, col: Int) = row in 0..<BOARD_SIZE && col in 0..<BOARD_SIZE

    private fun Cell.isFree() = state == State.FREE

    private fun canCapture(position: Int): List<Cell>{
        val cell=board[position]
        var ans = emptyList<Cell>();
        for (i in listOf(cell.up(), cell.down(), cell.left(), cell.right())){
            i ?: continue
            if(i.state == player.other.state)
                ans = ans + search(i.id, emptyList(), player.other.state, cell)
        }

    return ans
    }

    private fun search(position: Int,visited:List<Cell>,state:State, src: Cell = board[position]): List<Cell> {
        val cell=board[position]
        if(cell in visited)
            return emptyList()
        var list = emptyList<Cell>()
        for(i in listOf(cell.up(), cell.right(), cell.down(), cell.left())){
            i ?: continue
            if(i == src) continue
            when(i.state){
                state -> list = list + search(i.id, visited + cell, state, src)
                State.FREE -> return emptyList()
                else -> continue
            }
        }
        return list + cell
    }

    private fun Cell.up() = if(row == 1) null else board[id - BOARD_SIZE]
    private fun Cell.right() = if(col == BOARD_SIZE) null else board[id + 1]

    private fun Cell.left() = if(col == 1) null else board[id - 1]

    private fun Cell.down() = if(row == BOARD_SIZE) null else board[id + BOARD_SIZE]

    private fun cantBeCaptured(position: Int) = search(position, emptyList(), player.state).isEmpty()

    fun play(str: String): Pair<Board,Int> {
        val position = toPosition(str)
        require(board[position].isFree()){"Illegal move"}
        val toRemove = canCapture(position)
        return if(toRemove.isNotEmpty()) Pair(copy(board=board.mapIndexed{idx,value->
            when {
                idx == position->Cell(position, player.state)
                toRemove.any{ it.id == idx} -> Cell(position, State.FREE)
                else -> value
            }
        },turn=turn + 1
        ),toRemove.size)
        else if (cantBeCaptured(position)) Pair(copy(
            board=board.mapIndexed{idx, value -> if(idx == position) Cell(position, player.state) else value},
            turn = turn + 1
        ),0)
        else throw IllegalArgumentException("Illegal move")
    }

    private fun show(): String{
        val letters = ((0..<BOARD_SIZE).map { 'A' + it }).joinToString( prefix = " ".repeat(2), separator = " ")
        val lines = board
            .chunked(BOARD_SIZE)
            .joinToString(separator = "\n") {
                line -> line.joinToString(prefix = "${line.first().row} ", separator = " ") {
                    cell -> cell.state.value
                }
            }
        return (letters + "\n" + lines)
    }

    fun draw() = println(show())
}
