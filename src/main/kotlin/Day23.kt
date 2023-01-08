import utils.*
import utils.navigation.Direction8
import utils.navigation.Direction8.*

fun main() {
    Day23(IO.TYPE.SAMPLE).test(110, 20)
    Day23().solve()
}

class Day23(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
    override fun part1(): Int {
        val groove = Groove(input)

        repeat(10) {
            groove.proposePosition()
            groove.moveToPosition()
        }

        return groove.getNumberOfGroundTiles()
    }

    override fun part2(): Int {
        val groove = Groove(input)
        var round = 0
        while (true) {
            round++
            groove.proposePosition()
            if (groove.didElvesStop()) break
            groove.moveToPosition()
        }

        return round
    }

    class Groove(val input: String) {
        private val elf = "#"
        private val directionDecision = DirectionDecision()

        private val field = input.toGrid().toField()
        private val elves = field.search(elf).map { Elf(it) }.toSet()

        fun proposePosition() {
            elves.forEach { elf ->
                val possibleDirections = Direction8.values().filter { elf.position.doMovement(it) !in elves.map { it.position } }.toSet()
                val direction = directionDecision.findDirection(possibleDirections)
                elf.proposedPosition = if (direction == null) {
                    elf.position
                } else {
                    elf.position.doMovement(direction)
                }
            }
        }

        fun moveToPosition() {
            val duplicates = elves.groupingBy { it.proposedPosition }.eachCount().filter { it.value > 1 }.keys

            elves.forEach { elf ->
                elf.chooseNewPosition(duplicates)
            }
            directionDecision.rotate()
        }

        fun didElvesStop() = elves.all { it.position == it.proposedPosition }

        fun getNumberOfGroundTiles(): Int {
            val minX = elves.minOf { it.position.x }
            val maxX = elves.maxOf { it.position.x }
            val minY = elves.minOf { it.position.y }
            val maxY = elves.maxOf { it.position.y }

            return ((maxX - minX + 1) * (maxY - minY + 1) - elves.count())
        }

        private class DirectionDecision {
            private val ring = ArrayDeque(
                listOf(
                    setOf(NorthEast, North, NorthWest) to North,
                    setOf(SouthEast, South, SouthWest) to South,
                    setOf(NorthWest, West, SouthWest) to West,
                    setOf(NorthEast, East, SouthEast) to East,
                )
            )

            fun findDirection(freeDirections: Set<Direction8>): Direction8? {
                if (freeDirections.size == 8) return null
                for (i in ring.indices) {
                    val current = ring.get(i)
                    if (current.first.intersect(freeDirections).size == 3) {
                        return current.second
                    }
                }
                return null
            }

            fun rotate() {
                ring.addLast(ring.removeFirst())
            }
        }


        data class Elf(
            var position: Position,
            var proposedPosition: Position = Position.origin,
        ) {
            fun chooseNewPosition(duplicates: Set<Position>) {
                if (proposedPosition !in duplicates) {
                    position = proposedPosition
                }
            }
        }

    }
}           