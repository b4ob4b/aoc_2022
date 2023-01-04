import utils.Day
import utils.IO
import utils.matrix.Position
import utils.navigation.Direction4
import utils.navigation.Direction4.*
import utils.toGrid
import utils.toMatrix

fun main() {
    Day24(IO.TYPE.SAMPLE).test(18, 54)
    Day24().solve()
}

class Day24(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Blizzard Basin", inputType = inputType) {

    private val valleyOfTimes = mutableMapOf<Int, Valley>()
    private val field = input.toGrid()
    private val start = Position(0, 1)
    private val end = Position(field.size - 1, field.last().indexOf("."))
    private var timeStartToEnd = -1

    override fun part1(): Int {
        timeStartToEnd = bfs(start, end)
        return timeStartToEnd
    }

    override fun part2(): Int {
        val offset = bfs(end, start, timeStartToEnd) + timeStartToEnd
        return bfs(start, end, offset) + offset
    }

    private fun bfs(start: Position, end: Position, timeOffset: Int = 0): Int {
        val visited = mutableSetOf<Pair<Position, Int>>()

        val queue = ArrayDeque<Pair<Position, Int>>()
        queue.add(start to 0)

        var minimumTime = -1

        while (queue.isNotEmpty()) {
            val pair = queue.removeFirst()

            if (pair in visited) continue
            visited.add(pair)

            val position = pair.first
            val minute = pair.second + 1

            if (position == end) {
                minimumTime = minute
                break
            }

            val valley = valleyThroughTimes(minute + timeOffset)
            if (valley isBlockedFor position) continue

            val positions = position.get4Neighbours().toSet() + position

            positions.forEach { newPosition ->
                queue.add(newPosition to minute)
            }
        }
        return minimumTime
    }

    private fun valleyThroughTimes(minute: Int): Valley {
        if (minute in valleyOfTimes.keys) return valleyOfTimes[minute]!!
        return if (minute == 0) {
            valleyOfTimes[minute] = Valley(input.toGrid())
             valleyOfTimes[minute]!!
        } else {
            val valley = valleyThroughTimes(minute - 1)
            valley.moveBlizzards()
            valleyOfTimes[minute] = valley
            valleyOfTimes[minute]!!
        }
    }

    class Valley(field: List<List<String>>) {

        private val ground = field.toMatrix()
        private val wall = "#"
        private val walls = ground.search(wall).toSet()
        private var blizzards = field.parseBlizzards()

        fun moveBlizzards() {
            blizzards = blizzards.map { move(it) }
        }

        infix fun isBlockedFor(position: Position): Boolean {
            if (position in walls) return true
            if (hasBlizzardIn(position)) return true
            return position.row !in ground.rowIndices || position.col !in ground.colIndices
        }

        private infix fun hasBlizzardIn(position: Position) = position in blizzards.map { it.position }.toSet()

        private fun move(blizzard: Blizzard): Blizzard {
            val (position, direction) = blizzard
            val target = position.moveTo(direction)
            val newPosition = if (target in walls) {
                when (direction) {
                    North -> Position(ground.numberOfRows - 2, target.col)
                    East -> Position(target.row, 1)
                    South -> Position(1, target.col)
                    West -> Position(target.row, ground.numberOfCols - 2)
                }
            } else {
                target
            }
            return Blizzard(newPosition, direction)
        }

        private fun List<List<String>>.parseBlizzards() = this.flatMapIndexed { row, rows ->
            rows.mapIndexedNotNull { col, s ->
                if (s.isBlizzard()) Blizzard(Position(row, col), s.toDirection()) else null
            }
        }

        private fun String.isBlizzard() = """[>v^<]""".toRegex().containsMatchIn(this)


        private fun String.toDirection() = when (this) {
            ">" -> East
            "v" -> South
            "<" -> West
            "^" -> North
            else -> throw Exception("unknown direction: $this")
        }

        data class Blizzard(val position: Position, val direction: Direction4)

    }
}
