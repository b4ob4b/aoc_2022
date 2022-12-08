import utils.*
import kotlin.math.abs

fun main() {
    Day08(IO.TYPE.SAMPLE).test(21, 8)
    Day08().solve()
}

class Day08(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    data class Edge(val rows: IntRange, val cols: IntRange, val watchDirection: Position)

    private val trees = input.splitLines().map { it.toList().map { it.toString().toInt() } }
    private val maxEdge = trees.size - 1
    private val visibleTrees = mutableSetOf<Position>()

    override fun part1(): Int {
        val edge = 1 until maxEdge

        val edges = listOf(
            Edge(0..0, edge, Position(1, 0)), // upper, watch down
            Edge(edge, maxEdge..maxEdge, Position(0, -1)), // right, watch left
            Edge(maxEdge..maxEdge, edge, Position(-1, 0)), // lower, watch up
            Edge(edge, 0..0, Position(0, 1)), // left, watch right
        )


        edges.forEach { (rows, cols, direction) ->
            rows.forEach { row ->
                cols.forEach { col ->
                    val traverseTrees = ((edge.first)..edge.last).iterator()
                    var treeSize = trees[row][col]
                    var nextRow = row + if (direction.x != 0) direction.x * traverseTrees.nextInt() else 0
                    var nextCol = col + if (direction.y != 0) direction.y * traverseTrees.nextInt() else 0
                    var nextTree = trees[nextRow][nextCol]

                    while (traverseTrees.hasNext()) {
                        if (nextTree > treeSize) {
                            visibleTrees.add(Position(nextRow, nextCol))
                            treeSize = nextTree
                        }
                        nextRow = row + if (direction.x != 0) direction.x * traverseTrees.nextInt() else 0
                        nextCol = col + if (direction.y != 0) direction.y * traverseTrees.nextInt() else 0
                        nextTree = trees[nextRow][nextCol]
                    }
                    if (nextTree > treeSize) {
                        visibleTrees.add(Position(nextRow, nextCol))
                    }
                }
            }
        }


        return visibleTrees.size + 4 + edge.toList().size * 4
    }

    private fun Int.toSingleRange() = this..this

    fun List<List<Int>>.getViewDistanceOf(treePosition: Position, direction: Direction): Int {
        val movement = when (direction) {
            Direction.up -> (treePosition.x - 1) downTo 0 to treePosition.y.toSingleRange()
            Direction.down -> (treePosition.x + 1)..maxEdge to treePosition.y.toSingleRange()
            Direction.right -> treePosition.x.toSingleRange() to (treePosition.y + 1)..maxEdge
            Direction.left -> treePosition.x.toSingleRange() to ((treePosition.y - 1) downTo 0)
        }
        val treeSize = this[treePosition.x][treePosition.y]
        val (rows, cols) = movement
        val isSmaller = rows.flatMap { row ->
            cols.map { col ->
                this[row][col] < treeSize
            }
        }.takeWhile { it }
        if (isSmaller.size <= listOf(rows, cols).map { abs(it.last - it.first) }.max())
            return isSmaller.size + 1
        else
            return isSmaller.size

    }

    override fun part2(): Int {
        return visibleTrees.map { treePosition ->
            listOf(
                trees.getViewDistanceOf(treePosition, Direction.up),
                trees.getViewDistanceOf(treePosition, Direction.right),
                trees.getViewDistanceOf(treePosition, Direction.down),
                trees.getViewDistanceOf(treePosition, Direction.left),
            )
        }.map { it.reduce { acc, i -> acc * i } }.max()
    }
}           