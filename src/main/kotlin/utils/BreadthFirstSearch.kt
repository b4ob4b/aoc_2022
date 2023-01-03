package utils

import utils.matrix.Position
import utils.matrix.Matrix

class BreadthFirstSearch<T>(private val matrix: Matrix<T>) {

    fun findShortestPathBetween(from: Position, to: Position, test: (position: Position, neighbour: Position) -> Boolean): List<Position> {
        val queue = ArrayDeque<Pair<Position, List<Position>>>()
        val goal = matrix[to]
        queue.add(Pair(from, listOf(from)))
        val visited = mutableSetOf<Position>()
        while (queue.isNotEmpty()) {
            val (position, path) = queue.removeFirst()
            if (visited.contains(position)) continue
            visited.add(position)
            if (matrix[position] == goal) return path
            position.get4Neighbours().forEach { neighbour ->
                if (neighbour.row in matrix.rowIndices && neighbour.col in matrix.colIndices) {
                    if (test(position, neighbour) || matrix[neighbour] == goal) {
                        queue.add(neighbour to path + neighbour)
                    }
                }
            }
        }
        return emptyList()
    }

}
