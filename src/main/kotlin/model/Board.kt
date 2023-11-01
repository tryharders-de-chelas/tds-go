package model

data class Board(
    val board: List<Cell> = List(BOARD_SIZE * BOARD_SIZE){ Cell(it) },
    val turn: Int = 1,
    val pass: Pair<Int?, Int?> = null to null
) {
    private val player: Player get() = if(turn % 2 == 0) Player.WHITE else Player.BLACK

    private fun isInBounds(position: Int) = position in 0..<(BOARD_SIZE * BOARD_SIZE)

    private fun toPosition(str: String): Int {
        require(
            (str.length == 2) || (str.length == 3 && str.indexOfFirst{ it.isLetter() } != 1)
        ){"Invalid format"}
        val column: Int? = str.find{ it.isLetter() }?.lowercaseChar()?.code?.minus('a'.code)
        val line: Int? = str.filter{ it.isDigit()}.toIntOrNull()
        requireNotNull(column){"Invalid column"}
        requireNotNull(line){"Invalid line"}
        return ((line - 1) * BOARD_SIZE) + column
    }

    private fun isLegal(position: Int) = isFree(position) && isInBounds(position)

    private fun canCapture(position: Int): List<Cell>{
        val cell=board[position]
        val up = searchSurround(cell.up())
        val down =searchSurround(cell.down())
        val left = searchSurround(cell.left())
        val right =searchSurround(cell.right())
    return up+down+left+right
    }

    private fun searchSurround(cell: Cell?):List<Cell>{
        if (cell==null) return listOf()
        return search(cell.id, listOf(board[cell.id]),player.other.state) ?: return listOf()
    }
    private fun isFree(position: Int) = board[position].state == State.FREE


    private fun cantBeCaptured(position: Int):Boolean{
        return search(position,listOf(board[position]),player.state)==null
    }

    private fun search(position: Int,visited:List<Cell>,state:State):List<Cell>?{
        val cell=board[position]
        val up = confirmCell(cell.up(), visited,state) ?: return null
        val down = confirmCell(cell.down(), visited,state) ?: return null
        val left = confirmCell(cell.left(), visited,state) ?: return null
        val right = confirmCell(cell.right(), visited,state) ?: return null
        return up+down+left+right
    }

    private fun confirmCell(cell:Cell?,visited:List<Cell>,state:State):List<Cell>?{
        if(cell !in visited){
            if(cell?.state==State.FREE ){
                return null
            }else if(cell!=null && cell.state==state){
                return search(cell.id,visited+cell,state)
            }
        }
        return visited
    }


    //TODO("Make it work for cases in which a group of more than 2 cells are surrounded")

    private fun Cell.up() = if(row == 1) null else board[id - BOARD_SIZE]
    private fun Cell.right() = if(col == BOARD_SIZE) null else board[id + 1]

    private fun Cell.left() = if(col == 1) null else board[id - 1]

    private fun Cell.down() = if(row == BOARD_SIZE) null else board[id + BOARD_SIZE]

    operator fun get(str: String) = board[toPosition(str)].state

    fun play(str: String): Pair<Board,Int> {
        val position = toPosition(str)
        require(isLegal(position)){"Illegal move"}
        val toRemove = canCapture(position)
        return if(toRemove.isNotEmpty()) Pair(copy(board=board.mapIndexed{idx,value->
            when {
                idx == position->Cell(position, player.state)
                (Cell(idx,player.other.state) in toRemove) -> Cell(position,State.FREE)
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

    fun draw(){
        println(((0..<BOARD_SIZE).map { 'A' + it }).joinToString( prefix = " ".repeat(2), separator = " "))
        board.chunked(BOARD_SIZE){
            line -> println(
                line.joinToString(prefix = "${line.first().row} ", separator = " "){
                    it.state.value
                }
            )
        }
    }
}
