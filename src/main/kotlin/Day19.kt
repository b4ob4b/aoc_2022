import utils.*
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil
import kotlin.math.min

fun main() {
    Day19(IO.TYPE.SAMPLE).test(33, 56 * 62)
    Day19().solve()
}

class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Not Enough Minerals", inputType = inputType) {

    private val bluePrints = input.parseBluePrints()

    override fun part1() = bluePrints
        .mapIndexed { index, bluePrint ->
            val bluePrintId = index + 1
            val geodes = bfs(bluePrint, 24)
            geodes * bluePrintId
        }.sum()

    override fun part2() = bluePrints
        .take(3)
        .map { bluePrint ->
            bfs(bluePrint, 32, false)
        }.product()


    private fun bfs(bluePrint: List<Robot>, minutes: Int, part1: Boolean = true): Int {
        val seen = mutableSetOf<RobotFactory>()
        val queue = ArrayDeque<RobotFactory>()
        queue.add(RobotFactory(0, MineralMap(Mineral.Ore to 1)))

        val goal = minutes
        var maxGeodes = 0

        while (queue.isNotEmpty()) {
            val factory = queue.removeFirst()

            seen.add(factory) || continue

            val minute = factory.minute + 1
            val timeLeft = goal - minute

            val savings = factory.savings
            val robots = factory.robots

            if (savings.of(Mineral.Geode) > maxGeodes) {
                maxGeodes = savings.of(Mineral.Geode)
            }

            if (minute > goal) continue

            for (mineral in Mineral.values()) {

                val needRobot = robots.of(mineral) * (goal - minute) + savings.of(mineral) < (goal - minute) * bluePrint.maxOf(mineral)
                val savingsFull = savings.of(mineral) <= (bluePrint.maxOf(mineral) + 5)

                val needMineral = (needRobot && savingsFull) || mineral == Mineral.Geode
                val cost = bluePrint.single { it.type == mineral }.cost
                val readyToProduce = robots.contains(MineralMap(cost.map.keys.associateWith { 1 }))

                if (needMineral && readyToProduce) {
                    val timeToProduce = (cost - savings) / robots
                    val newRobots = robots + factory.robotUnderConstruction
                    val robotUnderConstruction = MineralMap(mineral to 1)
                    if (timeToProduce < 0) {
                        val newSavings = savings - cost + newRobots
                        queue.add(RobotFactory(minute + 1, newRobots, newSavings, robotUnderConstruction))

                    } else if (timeToProduce <= timeLeft) {
                        val newSavings = savings + newRobots * (timeToProduce + 1) - cost
                        queue.add(RobotFactory(minute + timeToProduce, newRobots, newSavings, robotUnderConstruction))
                        if (part1 && factory.waitingTime <= 1) {
                            queue.add(
                                RobotFactory(
                                    minute,
                                    robots + factory.robotUnderConstruction,
                                    savings + robots + factory.robotUnderConstruction,
                                    waitingTime = factory.waitingTime + 1
                                )
                            )
                        }
                    } else {
                        queue.add(RobotFactory(minute + timeLeft, newRobots, savings + newRobots * (timeLeft + 1)))
                    }
                }
            }
        }
        return maxGeodes
    }

    data class Robot(val cost: MineralMap, val type: Mineral)

    enum class Mineral {
        Ore, Clay, Obsidian, Geode
    }

    private fun List<Robot>.maxOf(mineral: Mineral) = this.maxOfOrNull { it.cost.map[mineral] ?: 0 } ?: 0

    data class RobotFactory(
        val minute: Int,
        val robots: MineralMap,
        val savings: MineralMap = MineralMap(),
        val robotUnderConstruction: MineralMap = MineralMap(),
        val waitingTime: Int = 0
    )

    data class MineralMap(val map: Map<Mineral, Int> = emptyMap()) {
        constructor(pair: Pair<Mineral, Int>) : this(mapOf(pair))

        operator fun plus(other: MineralMap): MineralMap {
            val minerals = map.keys + other.map.keys
            val newMap = minerals.associateWith { mineral ->
                (map[mineral] ?: 0) + (other.map[mineral] ?: 0)
            }
            return MineralMap(newMap)
        }

        operator fun minus(other: MineralMap): MineralMap {
            val minerals = map.keys + other.map.keys
            val newMap = minerals.associateWith { mineral ->
                (map[mineral] ?: 0) - (other.map[mineral] ?: 0)
            }
            return MineralMap(newMap)
        }

        operator fun div(other: MineralMap): Int {
            if (!other.map.keys.containsAll(map.keys)) throw Exception("can't divide $this by $other")
            return map.entries.maxOf { (mineral, amount) ->
                ceil(amount / other.map[mineral]!!.toDouble()).toInt()
            }
        }

        operator fun times(time: Int) = MineralMap(
            map.entries.associate { (mineral, amount) ->
                mineral to amount * time
            }
        )

        fun contains(other: MineralMap): Boolean {
            val minerals = map.keys + other.map.keys
            return minerals.all { mineral ->
                (map[mineral] ?: 0) >= (other.map[mineral] ?: 0)
            }
        }

        fun of(mineral: Mineral) = map[mineral] ?: 0
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
        return Robot(MineralMap(costs), type)
    }

    private fun String.parseBluePrints() = this.splitLines()
        .map { it.split(""": |\. |\.""".toRegex()).drop(1).dropLast(1) }
        .map { it.toBlueprint() }

}
           