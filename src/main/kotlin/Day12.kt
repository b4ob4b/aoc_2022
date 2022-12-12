import utils.*

fun main() {
    Day12(IO.TYPE.SAMPLE).test(31, 29)
    Day12().solve()
}

class Day12(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val heightMap = input.toGrid { it[0].toHeight() }.toMatrix()
    private val goal = 'E'.toHeight()

    private fun Char.toHeight() = when (this) {
        'S' -> 0
        'E' -> 26
        else -> this.code - 'a'.code
    }

    private fun Matrix<Int>.breadthFirstSearch(queue: ArrayDeque<Pair<Position, Int>>, goal: Int): Int {
        val visited = mutableSetOf<Position>()
        while (queue.isNotEmpty()) {
            val (pos, steps) = queue.removeFirst()
            if (visited.contains(pos)) continue
            visited.add(pos)
            if (this[pos] == goal) return steps
            pos.get4Neighbours().forEach { neighbour ->
                val currentHeight = this[pos]
                if (neighbour.x in this.rowIndices && neighbour.y in this.colIndices) {
                    val neighbourHeight = this[neighbour]
                    if (neighbourHeight <= (1 + currentHeight)) {
                        queue.add(neighbour to (steps + 1))
                    }
                }
            }
        }
        return -1
    }

    override fun part1(): Int {
        val queue = heightMap.search(0).first()
            .let { position ->
                ArrayDeque<Pair<Position, Int>>().also { it.add(position to 0) }
            }
        return heightMap.breadthFirstSearch(queue, goal)
    }

    override fun part2(): Int {
        val queue = heightMap.search(0)
            .fold(ArrayDeque<Pair<Position, Int>>()) { queue, position ->
                queue.also { it.add(position to 0) }
            }
        return heightMap.breadthFirstSearch(queue, goal)
    }
}           