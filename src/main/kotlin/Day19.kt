import utils.Day
import utils.IO
import utils.print
import utils.splitLines
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.min

fun main() {
    Day19(IO.TYPE.SAMPLE).test(33)
    Day19().solve()
}

class Day19(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val bluePrints = input.splitLines()
        .map { it.split(""": |\. |\.""".toRegex()).drop(1).dropLast(1) }
        .map { it.toBlueprint() }

    data class Robot(val cost: MineralMap, val type: Mineral)

    enum class Mineral {
        Ore, Clay, Obsidian, Geode;

        fun toChoice() = Choice.valueOf(toString())
    }

    enum class Choice {
        Ore, Clay, Obsidian, Geode, Save;

        fun toMineral() = Mineral.valueOf(toString())
    }

    private fun List<Robot>.maxOf(mineral: Mineral) = this.maxOfOrNull { it.cost.map[mineral] ?: 0 } ?: 0

    data class RobotFactory(
        val minute: Int,
        val robots: MineralMap,
        val robotUnderConstruction: MineralMap = MineralMap(),
        val savings: MineralMap = MineralMap()
    )

    override fun part1(): Any? {
        return bluePrints.mapIndexed { index, bluePrint ->
            val bluePrintId = index + 1

            val seen = mutableSetOf<RobotFactory>()
            val queue = ArrayDeque<RobotFactory>()
            queue.add(RobotFactory(0, MineralMap(mapOf(Mineral.Ore to 1))))

            val goal = 24

            val geodes = mutableSetOf<Int>()

            while (queue.isNotEmpty()) {
                val factory = queue.removeFirst()

                if (factory in seen) continue
                seen.add(factory)


                val minute = factory.minute + 1
                val savings = factory.robots + factory.savings
                val robots = factory.robots + factory.robotUnderConstruction

                if (minute == goal) {
                    geodes.add(savings.of(Mineral.Geode))
                }

                if (minute == goal + 1) {
                    break
                }

                val choices = buildList {
                    Mineral.values().forEach { mineral ->
                        val needMineral =
                            (robots.of(mineral) < bluePrint.maxOf(mineral) && savings.of(mineral) < bluePrint.maxOf(mineral) * 1.5) || mineral == Mineral.Geode
                        val canBuyRobotWithMineral = savings.contains(bluePrint.single { it.type == mineral }.cost)
                        if (needMineral && canBuyRobotWithMineral) {
                            add(mineral.toChoice())
                        }
                    }
                    add(Choice.Save)
                }

                choices.forEach { choice ->
                    if (choice == Choice.Save) {
                        queue.add(RobotFactory(minute, robots, MineralMap(), savings))
                    } else {
                        val newRobot = MineralMap(choice.toMineral() to 1)
                        val cost = bluePrint.single { it.type == choice.toMineral() }.cost
                        queue.add(RobotFactory(minute, robots, newRobot, savings - cost))
                    }
                }
            }

            geodes.max() * bluePrintId
        }.sum()
    }

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

        fun contains(other: MineralMap): Boolean {
            val minerals = map.keys + other.map.keys
            return minerals.all { mineral ->
                (map[mineral] ?: 0) >= (other.map[mineral] ?: 0)
            }
        }

        fun of(mineral: Mineral) = map[mineral] ?: 0
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
        return Robot(MineralMap(costs), type)
    }


}
           