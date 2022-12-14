import utils.*
import kotlin.math.sign

fun main() {
    Day09(IO.TYPE.SAMPLE).test(13)
    Day09(IO.TYPE.SAMPLE2).test(part2 = 36)
    Day09().solve()
}

class Day09(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val commands = input.splitLines()
        .map { it.split(" ") }
        .map { (direction, distance) -> Command(direction.toDirection(), distance.toInt()) }

    private val pathHead = commands.fold(listOf(Position(0, 0))) { acc, command ->
        val currentPosition = acc.last()
        acc + (1..command.distance).runningFold(currentPosition) { acc, i ->
            acc.doMovement(command.direction)
        }
    }

    data class Command(val direction: Direction4, val distance: Int)

    private fun String.toDirection() = when (this) {
        "R" -> Direction4.East
        "U" -> Direction4.North
        "D" -> Direction4.South
        "L" -> Direction4.West
        else -> Direction4.South
    }

    private fun Position.isNearBy(other: Position) = this.get8Neighbours().contains(other) || this == other

    private fun Position.follow(other: Position) =
        if (this.isNearBy(other)) this else Position(this.x + (other.x - this.x).sign, this.y + (other.y - this.y).sign)

    override fun part1(): Int {
        return pathHead
            .fold(listOf(Position(0, 0))) { tail, head ->
                tail + tail.last().follow(head)
            }
            .toSet()
            .size
    }

    override fun part2(): Int {
        return pathHead.runningFold((1..9).map { Position(0, 0) }) { knots, head ->
            knots.runningFold(head) { knot, nextKnot ->
                nextKnot.follow(knot)
            }.drop(1)
        }.map { it.last() }.toSet().size
    }
}           