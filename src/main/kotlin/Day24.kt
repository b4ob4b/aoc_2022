import utils.Day
import utils.IO
import utils.matrix.Matrix
import utils.matrix.Position
import utils.navigation.Direction4
import utils.navigation.Direction4.*
import utils.print
import utils.toGrid
import utils.toMatrix
import java.lang.Exception

fun main() {
    Day24(IO.TYPE.SAMPLE).test(18)
    Day24().solve()
}

class Day24(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Blizzard Basin", inputType = inputType) {

    private val valleyToTime = mutableMapOf<Int, Valley>()

    private fun getValleyOf(minute: Int): Valley {
        if (minute in valleyToTime.keys) return valleyToTime[minute]!!
        if (minute == 0) {
            valleyToTime[minute] = Valley(input.toGrid())
            return valleyToTime[minute]!!
        } else {
            val valley = getValleyOf(minute - 1)
            valley.moveBlizzards()
            valleyToTime[minute] = valley
            return valleyToTime[minute]!!
        }
    }

    override fun part1(): Any? {
        val field = input.toGrid()

        val start = Position(0, 1)
        val end = Position(field.size - 1, field.last().indexOf("."))
        val visited = mutableSetOf<Triple<Position, Int, Position>>()

        val queue = mutableListOf<Triple<Position, Int, Position>>()
        queue.add(Triple(start, 0, start))

        while (queue.isNotEmpty()) {

//            val pair = queue.minBy { (end - it.first).manhattenDistance  }
//            393
            val triple = queue.first()
            queue.remove(triple)
            val (position, oldMinute, _) = triple
            if (triple in visited) continue
            visited.add(triple)
            println("${oldMinute}: ${(end - position).manhattenDistance}: ${queue.size}")
            val minute = oldMinute + 1
            val valley = getValleyOf(minute)
            if (position == end) {
                return minute.print()

            }

            if (valley hasNoGroundIn position) continue

            val positions = position.get4Neighbours().toSet() + position

            positions.forEach { neighbour ->

//                    minute.print()
//                    valley.position = position
//                    valley.print()

                queue.add(Triple(neighbour, minute, position))
            }

        }

        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    class Valley(field: List<List<String>>) {

        private val ground = field.toMatrix()
        private val wall = "#"
        private val walls = ground.search(wall).toSet()
        var blizzards = field.parseBlizzards()
        var position = Position(0, 0)


        fun moveBlizzards() {
            blizzards = blizzards.map { move(it) }
        }

        infix fun hasGroundIn(position: Position): Boolean {
            if (position in walls) return false
            if (hasBlizzardIn(position)) return false
            return position.row in ground.rowIndices && position.col in ground.colIndices
        }

        infix fun hasNoGroundIn(position: Position): Boolean {
            return hasGroundIn(position).not()
        }

        infix fun hasBlizzardIn(position: Position) = position in blizzards.map { it.position }.toSet()

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

        private fun String.isBlizzard() = """[>v\^<]""".toRegex().containsMatchIn(this)


        private fun String.toDirection() = when (this) {
            ">" -> East
            "v" -> South
            "<" -> West
            "^" -> North
            else -> throw Exception("unknown direction: $this")
        }

        private fun Direction4.asString() = when (this) {
            East -> ">"
            South -> "v"
            West -> "<"
            North -> "^"
        }

        private fun List<Blizzard>.asPositionStringPair(): Map<Position, String> {
            val duplicates = this.groupingBy { it.position }.eachCount().filter { it.value > 1 }

            return this.associate {
                val position = it.position
                if (position in duplicates.keys) {
                    position to duplicates[position].toString()
                } else {
                    it.position to it.direction.asString()
                }
            }
        }

        data class Blizzard(val position: Position, val direction: Direction4)

        override fun toString(): String {
            return Matrix(ground.numberOfRows, ground.numberOfCols) { "." }
                .insertAt(walls.associateWith { "#" })
                .insertAt(blizzards.asPositionStringPair())
                .insertAt(position, if (position in blizzards.map { it.position }) "X" else "E")
                .toString()
        }

    }
}
