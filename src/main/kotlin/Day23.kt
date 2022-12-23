import utils.*

fun main() {
    Day23(IO.TYPE.SAMPLE2).test(1)
    Day23().solve()
}

class Day23(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val elf = "#"

    data class Elf(
        var position: Position,
        var possibleDirections: Set<Direction8> = emptySet(),
        var proposedPosition: Position = Position.origin,
    ) {
        private var stepNumber = 0
        fun getProposedDirection(): Direction8 {
            val conversions = listOf(
                setOf(Direction8.NorthEast, Direction8.North, Direction8.NorthWest) to Direction8.North,
                setOf(Direction8.SouthEast, Direction8.South, Direction8.SouthWest) to Direction8.South,
                setOf(Direction8.NorthWest, Direction8.West, Direction8.SouthWest) to Direction8.West,
                setOf(Direction8.NorthEast, Direction8.East, Direction8.SouthEast) to Direction8.East,
            )
            for (i in conversions.indices) {
                val j = (i + stepNumber) % conversions.size
                if (conversions[j].first.intersect(possibleDirections).size == 3) {
                    stepNumber++
                    return conversions[j].second
                }
            }
            throw Exception("no directions found")
        }
    }

    override fun part1(): Any? {
        val field = input.toGrid().toField()

        val elfs = field.search(elf).map { Elf(it) }.toSet()
        val emptyField = field.insertAt(elfs.associate { it.position to "." })

        while (true) {
            elfs.forEach { elf ->
                elf.possibleDirections = Direction8.values().filter { elf.position.doMovement(it) !in elfs.map { it.position } }.toSet()
                if (elf.possibleDirections.size < 8) {
                    val direction = elf.getProposedDirection()
                    elf.proposedPosition = elf.position.doMovement(direction)
                } else {
                    elf.proposedPosition = elf.position
                }
            }

            if (elfs.all { it.position == it.proposedPosition }) break

            val duplicates = elfs.groupingBy { it.proposedPosition }.eachCount().filter { it.value > 1 }.keys

            elfs.forEach { elf ->
                if (elf.proposedPosition !in duplicates) {
                    elf.position = elf.proposedPosition
                }
            }
        }

        emptyField.insertAt(elfs.associate { it.position to elf }).print()
        println()

        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           