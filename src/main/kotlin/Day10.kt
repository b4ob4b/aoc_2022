import utils.Day
import utils.IO
import utils.print
import utils.splitLines

fun main() {
    Day10(IO.TYPE.SAMPLE2).test(13140)
    Day10().solve()
}

class Day10(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Cathode-Ray Tube", inputType = inputType) {

    data class Command(val startCylce: Int, val endCycle: Int, val amountToAdd: Int = 0)

    private fun String.toCommand(startCycle: Int): Command {
        return if (this == "noop") Command(startCycle, startCycle)
        else Command(startCycle, startCycle + 1, this.split(" ")[1].toInt())
    }

    private val commands = input.splitLines()
        .fold(emptyList<Command>()) { acc, string ->
            val nextStartCycle = if (acc.isEmpty()) 1 else acc.last().endCycle + 1
            acc + string.toCommand(nextStartCycle)
        }

    private val cycles = commands
        .fold(listOf(1)) { acc, command ->
            val value = acc.last() + command.amountToAdd
            acc + (1..(command.endCycle - command.startCylce)).map { acc.last() } + (command.amountToAdd + acc.last())
        }
    
    private val cyclesToCheck = listOf(20, 60, 100, 140, 180, 220)

    override fun part1(): Int {
        return cycles
            .mapIndexed { index, value -> index + 1 to value }
            .filter { cyclesToCheck.contains(it.first) }
            .sumOf { it.first * it.second }
    }


    override fun part2(): String {
        val rowLength = 40
        val rowIndices = (0..5).map { row -> (0 until rowLength).map { it + rowLength * row } }

        rowIndices
            .joinToString("\n") { row ->
                row.joinToString("") { crt ->
                    val sprite = (cycles[crt] - 1)..(cycles[crt] + 1)
                    if (sprite.contains(crt % rowLength)) "#" else "."
                }
            }
            .print()

        return "PGHFGLUG"
    }
}           