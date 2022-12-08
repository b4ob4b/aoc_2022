import utils.*
import kotlin.math.abs

fun main() {
    Day08(IO.TYPE.SAMPLE).test(21, 8)
    Day08().solve()
}

class Day08(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val trees = input.splitLines().map { it.toList().map { it.toString().toInt() } }

    data class Edge(val rows: IntRange, val cols: IntRange, val watchDirection: Position)

    private val maxEdge = trees.size - 1
    private val visibleTrees = mutableSetOf<Position>()
    private val edge = 1 until maxEdge
    private val edges = listOf(
        Edge(0.toSingleRange(), edge, Position(1, 0)), // upper, watch down
        Edge(edge, maxEdge.toSingleRange(), Position(0, -1)), // right, watch left
        Edge(maxEdge.toSingleRange(), edge, Position(-1, 0)), // lower, watch up
        Edge(edge, 0.toSingleRange(), Position(0, 1)), // left, watch right
    )

    override fun part1(): Int {
        edges.forEach { (rows, cols, direction) ->
            rows.forEach { row ->
                cols.forEach { col ->
                    val traverseTrees = (edge.first..edge.last).iterator()
                    var treeSize: Int = trees[row][col]
                    var nextRow: Int
                    var nextCol: Int
                    var nextTreeSize: Int

                    do {
                        nextRow = row + if (direction.x != 0) direction.x * traverseTrees.nextInt() else 0
                        nextCol = col + if (direction.y != 0) direction.y * traverseTrees.nextInt() else 0
                        nextTreeSize = trees[nextRow][nextCol]
                        if (nextTreeSize > treeSize) {
                            visibleTrees.add(Position(nextRow, nextCol))
                            treeSize = nextTreeSize
                        }
                    } while (traverseTrees.hasNext())
                }
            }
        }
        val amountCorners = 4
        val amountEdges = edge.toList().size * 4
        return visibleTrees.size + amountCorners + amountEdges
    }

    private fun Int.toSingleRange() = this..this

    private fun List<List<Int>>.getViewDistanceOf(treePosition: Position, direction: Direction): Int {
        val movement = when (direction) {
            Direction.up -> (treePosition.x - 1) downTo 0 to treePosition.y.toSingleRange()
            Direction.right -> treePosition.x.toSingleRange() to (treePosition.y + 1)..maxEdge
            Direction.down -> (treePosition.x + 1)..maxEdge to treePosition.y.toSingleRange()
            Direction.left -> treePosition.x.toSingleRange() to ((treePosition.y - 1) downTo 0)
        }
        val treeSize = this[treePosition.x][treePosition.y]
        val (rows, cols) = movement
        val isSmaller = rows.flatMap { row ->
            cols.map { col ->
                this[row][col] < treeSize
            }
        }.takeWhile { it }
        return if (isSmaller.size <= listOf(rows, cols).maxOf { abs(it.last - it.first) })
            isSmaller.size + 1
        else
            isSmaller.size

    }

    override fun part2(): Int {
        return visibleTrees.map { treePosition ->
            listOf(
                trees.getViewDistanceOf(treePosition, Direction.up),
                trees.getViewDistanceOf(treePosition, Direction.right),
                trees.getViewDistanceOf(treePosition, Direction.down),
                trees.getViewDistanceOf(treePosition, Direction.left),
            )
        }.maxOf { it.product() }
    }
}           