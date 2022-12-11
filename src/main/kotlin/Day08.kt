import utils.*

fun main() {
    Day08(IO.TYPE.SAMPLE).test(21, 8)
    Day08().solve()
}

class Day08(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val trees = input.toGrid(mapCell = String::toInt)

    private val maxEdge = trees.size - 1
    private val visibleTrees = mutableSetOf<Position>()
    private val edge = 1 until maxEdge

    private val viewLines = listOf(
        edge.map { x -> (0..maxEdge).map { y -> Position(x, y) }.asSequence() }, // left to right
        edge.map { y -> (0..maxEdge).map { x -> Position(x, y) }.asSequence() }, // top to bottom
        edge.map { x -> (maxEdge downTo 0).map { y -> Position(x, y) }.asSequence() }, // right to left
        edge.map { y -> (maxEdge downTo 0).map { x -> Position(x, y) }.asSequence() }, // bottom to top
    )

    override fun part1(): Int {
        val amountCorners = 4
        visibleTrees.addAll(
            viewLines.flatMap { viewLine ->
                viewLine.flatMap { positions ->
                    positions.lookForBiggerTrees()
                }
            }
        )
        return visibleTrees.size + amountCorners
    }

    override fun part2(): Int {
        return visibleTrees
            .filter { it.x != 0 && it.x != maxEdge && it.y != 0 && it.y != maxEdge }
            .maxOf { treePosition ->
                treePosition.getViewLines().map {
                    it.getViewRange(treePosition)
                }.product()
            }
    }

    private fun Sequence<Position>.lookForBiggerTrees() = this.takeWhile { trees[it.x][it.y] <= 9 }
        .fold(emptySet<Position>()) { visibleTrees, position ->
            if (visibleTrees.isEmpty()) return@fold setOf(position)
            val sizeBiggestTree = visibleTrees.last().let { trees[it.x][it.y] }
            if (trees[position.x][position.y] > sizeBiggestTree) {
                visibleTrees + position
            } else {
                visibleTrees
            }
        }

    private fun Position.getViewLines() = listOf(
        ((this.y + 1)..maxEdge).map { y -> Position(this.x, y) }, // left to right
        ((this.x + 1)..maxEdge).map { x -> Position(x, this.y) }, // top to bottom
        ((this.y - 1) downTo 0).map { y -> Position(this.x, y) }, // right to left
        ((this.x - 1) downTo 0).map { x -> Position(x, this.y) }, // bottom to top
    )

    private fun List<Position>.getViewRange(treePosition: Position): Int {
        val viewHeight = trees[treePosition.x][treePosition.y]
        val viewRange = this.takeWhile { trees[it.x][it.y] < viewHeight }.count()
        return if (viewRange < this.size) viewRange + 1 else viewRange
    }

}           