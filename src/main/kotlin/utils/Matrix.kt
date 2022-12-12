package utils

data class Matrix<T>(val matrix: List<List<T>>) {
    
    constructor(rows: Int, cols: Int, element: () -> T) : this(List(rows * cols) { element.invoke() }.chunked(cols))

    val numberOfRows = matrix.size
    val numberOfCols = matrix.first().size

    val rowIndices = 0 until numberOfRows
    val colIndices = 0 until numberOfCols

    operator fun get(position: Position) = matrix[position.x][position.y]

    fun <T> search(element: T) = sequence {
        (0 until numberOfRows).flatMap { row ->
            (0 until numberOfCols).map { col ->
                if (matrix[row][col] == element) yield(Position(row, col))
            }
        }
    }

    fun flipHorizontal(): Matrix<T> {
        return matrix.reversed().toMatrix()
    }

    fun flipVertical(): Matrix<T> {
        return matrix.map { it.reversed() }.toMatrix()
    }

    fun transpose(): Matrix<T> {
        return (0 until numberOfCols).map { col ->
            (0 until numberOfRows).map { row ->
                matrix[row][col]
            }
        }.toMatrix()
    }

    fun rotateClockWise() = this.transpose().flipVertical()

    fun rotateCounterClockWise() = this.transpose().flipHorizontal()
    
    fun <T> insertAt(position: Position, element: T): Matrix<T> {
        return matrix.mapIndexed { rowIndex, rows -> 
            rows.mapIndexed { colIndex, cell -> 
                if(rowIndex == position.x && colIndex == position.y) element else cell as T 
            }
        }.toMatrix()
    }

    fun <T> insertAt(positionMap: Map<Position,T>): Matrix<T> {
        return matrix.mapIndexed { rowIndex, rows ->
            rows.mapIndexed { colIndex, cell ->
                if(positionMap.containsKey(Position(rowIndex, colIndex))) positionMap[Position(rowIndex, colIndex)] as T else cell as T
            }
        }.toMatrix()
    }

    override fun toString() = matrix
        .joinToString("\n") { row ->
            row.joinToString(" ")
        }
}
