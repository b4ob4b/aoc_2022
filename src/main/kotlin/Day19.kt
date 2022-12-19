import utils.Day
import utils.IO
import utils.print
import utils.splitLines
import java.util.*

fun main() {
    Day19(IO.TYPE.SAMPLE).test(33)
    Day19().solve()
}

class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val bluePrints = input.splitLines()
        .map { it.split(""": |\. |\.""".toRegex()).drop(1).dropLast(1) }
        .map { it.toBlueprint() }

    data class Robot(val cost: Map<Mineral, Int>, val type: Mineral)

    enum class Mineral { Ore, Clay, Obsidian, Geode }


    override fun part1(): Any? {
        bluePrints
            .print()
        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    private fun List<String>.toBlueprint() = this.map { it.toRobot() }

    private fun String.toMineral() =
        Mineral.valueOf(this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })

    private fun String.toRobot(): Robot {
        val strings = this.split(" ")
        val type = strings[1].toMineral()
        val costs = buildMap {
            strings.slice(4 until strings.size).filter { it != "and" }.chunked(2).forEach { (amount, mineral) ->
                put(mineral.toMineral(), amount.toInt())
            }
        }
        return Robot(costs, type)
    }


}
           