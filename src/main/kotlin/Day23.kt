import utils.*

fun main() {
    Day23(IO.TYPE.SAMPLE2).test(1)
    Day23().solve()
}

class Day23(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val elf = "#"

    class DirectionDecision {
        val ring = ArrayDeque<Pair<Set<Direction8>, Direction8>>(
            listOf(
                setOf(Direction8.NorthEast, Direction8.North, Direction8.NorthWest) to Direction8.North,
                setOf(Direction8.SouthEast, Direction8.South, Direction8.SouthWest) to Direction8.South,
                setOf(Direction8.NorthWest, Direction8.West, Direction8.SouthWest) to Direction8.West,
                setOf(Direction8.NorthEast, Direction8.East, Direction8.SouthEast) to Direction8.East,
            )
        )

        fun findDirection(freeDirections: Set<Direction8>): Direction8? {
            for (i in ring.indices) {
                val current = ring.get(i)
                if (current.first.intersect(freeDirections).size == 3) {
                    ring.addLast(ring.removeAt(i))
                    return current.second
                }
            }
            return null
        }
    }

    data class Elf(
        var position: Position,
        var possibleDirections: Set<Direction8> = emptySet(),
        var proposedPosition: Position = Position.origin,
        val directionDecision: DirectionDecision = DirectionDecision()
    ) {
        fun getProposedDirection() = directionDecision.findDirection(possibleDirections)
        
        fun chooseNewPosition(duplicates: Set<Position>) {
            if (proposedPosition !in duplicates) {
                position = proposedPosition
            }
        }
    }

    override fun part1(): Any? {
        val field = input.toGrid().toField()

        val elfs = field.search(elf).map { Elf(it) }.toSet()
        val emptyField = field.insertAt(elfs.associate { it.position to "." })

        emptyField.insertAt(elfs.associate { it.position to elf }).print()
        println()

        repeat(10) {
            elfs.forEach { elf ->
                elf.possibleDirections = Direction8.values().filter { elf.position.doMovement(it) !in elfs.map { it.position } }.toSet()
                if (elf.possibleDirections.size < 8) {
                    val direction = elf.getProposedDirection()
                    if (direction == null) {
                        elf.proposedPosition = elf.position
                    } else {
                        elf.proposedPosition = elf.position.doMovement(direction)
                    }
                } else {
                    elf.proposedPosition = elf.position
                }
            }

//            if (elfs.all { it.position == it.proposedPosition }) break

            val duplicates = elfs.groupingBy { it.proposedPosition }.eachCount().filter { it.value > 1 }.keys

            elfs.forEach { elf ->
                elf.chooseNewPosition(duplicates)
            }
            println()
            println("round: ${it + 1}")
            emptyField.insertAt(elfs.associate { it.position to elf }).print()
            println()
        }


        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           