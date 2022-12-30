import utils.*
import utils.Direction4.*

fun main() {
    Day22(IO.TYPE.SAMPLE).test(6032)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    val map = Map(input)

    private val instructions = input.parseInstructions()

    override fun part1(): Int {
        val start = Santa(map.positions.first(), East)

        val finalSanta = instructions.fold(start) { santa, instruction ->
            santa.follow(instruction)
        }

        return calculateFinalPassword(finalSanta)
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    private fun String.parseInstructions() = this.split("\n\n").last()
        .let { instructions ->

            val numbers = instructions.split("""R|L""".toRegex()).map { it.toInt() }
            val rotations = instructions.split("""\d+""".toRegex()).filter(String::isNotBlank).map(Rotation::of)

            buildList {
                add(Forward(numbers.first()))
                numbers.drop(1).forEachIndexed { index, number ->
                    add(Turn(rotations[index]))
                    add(Forward(number))
                }
            }
        }

    data class Santa(val position: Position, val direction: Direction4)

    fun Santa.follow(instruction: Instruction): Santa {
        return when (instruction) {
            is Forward -> go(instruction.amount)
            is Turn -> turn(instruction.rotation)
        }
    }

    private fun Santa.go(amount: Int): Santa {
        return (1..amount)
            .asSequence()
            .runningFold(this) { santa, _ ->
                val position = map.move(santa.position, direction)
                Santa(position, direction)

            }
            .takeWhile { santa -> map.isOpenTile(santa.position) }
            .last()
    }

    private fun Santa.turn(rotation: Rotation): Santa {
        return when (rotation) {
            Rotation.Left -> when (this.direction) {
                North -> Santa(this.position, West)
                West -> Santa(this.position, South)
                South -> Santa(this.position, East)
                East -> Santa(this.position, North)
            }

            Rotation.Right -> when (this.direction) {
                North -> Santa(this.position, East)
                East -> Santa(this.position, South)
                South -> Santa(this.position, West)
                West -> Santa(this.position, North)
            }
        }
    }

    private fun calculateFinalPassword(santa: Santa): Int {
        val (col, row) = santa.position
        val facing = (santa.direction.ordinal + 3) % Direction4.values().size
        return 1000 * (row + 1) + 4 * (col + 1) + facing
    }

    sealed class Instruction()
    data class Forward(val amount: Int) : Instruction()
    data class Turn(val rotation: Rotation) : Instruction()
    enum class Rotation {
        Left, Right;

        companion object {
            fun of(string: String) = when (string) {
                "R" -> Right
                "L" -> Left
                else -> throw Exception("unknown direction: $string")
            }
        }
    }

    class Map(input: String) {
        val map = input.split("\n\n").first()
            .splitLines()
            .map { it.toList() }
        val positions = map.flatMapIndexed { row, rows ->
            rows.mapIndexed { col, char ->
                if (char.toString().isBlank()) null
                else Position(col, row)
            }.filterNotNull()
        }.toSet()

        fun move(position: Position, direction: Direction4): Position {
            val target = position.doMovement(direction, false)
            return if (target !in positions) {
                when (direction) {
                    North -> Position(target.x, positions.filter { it.x == target.x }.maxOf { it.y })
                    East -> Position(positions.filter { it.y == target.y }.minOf { it.x }, target.y)
                    South -> Position(target.x, positions.filter { it.x == target.x }.minOf { it.y })
                    West -> Position(positions.filter { it.y == target.y }.maxOf { it.x }, target.y)
                }
            } else target
        }

        fun isOpenTile(position: Position) = map[position.y][position.x] == '.'
    }
}           