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

    data class Robot(val cost: Map<Mineral, Int> = mapOf(), val type: Mineral)

    enum class Mineral { Ore, Clay, Obsidian, Geode }

    enum class Choice { Buy, Save }

    override fun part1(): Any? {
        dfs(bluePrints.first(), 24, listOf(Robot(type = Mineral.Ore)), emptyList()).print()
        return "not yet implement"
    }

    private fun dfs(blueprint: List<Robot>, time: Int, robots: List<Robot>, savings: List<Mineral>): Int {
        // collect
        val newSavings = robots.map { it.type } + savings
        val remainingTime = time - 1

        if (remainingTime == 0) return newSavings.filter { it == Mineral.Geode }.size

        // choice
        val robotsToBuy = blueprint.filter { blueprintRobot ->
            blueprintRobot.cost.all { (mineral, amount) ->
                val saving = newSavings.groupingBy { it }.eachCount()
                if (mineral !in saving.keys) false
                else newSavings.groupingBy { it }.eachCount()[mineral]!! >= amount
            }
        }

        if (robotsToBuy.isEmpty()) return dfs(blueprint, remainingTime, robots, newSavings)

        return Choice.values().maxOf { choice ->
            when (choice) {
                Choice.Save -> dfs(blueprint, remainingTime, robots, newSavings)
                Choice.Buy -> {
                    robotsToBuy.maxOf { robot ->
                        val savingsAfterPurchase = newSavings.toMutableList().also { list ->
                            robot.cost.forEach { (mineral, amount) ->
                                repeat(amount) { list.remove(mineral) }
                            }
                        }
                        dfs(blueprint, remainingTime, robots + robot, savingsAfterPurchase)
                    }
                }
            }
        }
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
           