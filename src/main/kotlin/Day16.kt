import utils.*

fun main() {
    Day16(IO.TYPE.SAMPLE).test(1651)
    Day16().solve()
}

class Day16(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    data class Valve(
        val name: String,
        val rate: Int,
        val tunnels: List<String>,
        var openedAt: Int = 0,
        var isOpen: Boolean = false
    )

    private val valves = input.splitLines()
        .map {
            it.split(" ").let { information ->
                Valve(
                    information[1],
                    information[4].split("""=|;""".toRegex())[1].toInt(),
                    information.slice(9 until information.size).map { it.removeSuffix(",") }
                )
            }
        }

    override fun part1(): Any? {
        val distances: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

        valves
            .filter { it.rate > 0 || it.name == "AA" }
            .forEach { startValve ->

                distances[startValve.name] = mutableMapOf()

                val queue = ArrayDeque<Pair<Valve, Int>>()
                val start = valves.single { it.name == "AA" }

                queue.add(start to 0)
                val visited = mutableSetOf<Valve>()
                while (queue.isNotEmpty()) {
                    val (valve, distance) = queue.removeFirst()
                    visited.add(valve)
                    valve.tunnels.map { name -> valves.single { it.name == name } }
                        .filter { it !in visited }
                        .forEach { neighbour ->
                            queue.add(neighbour to distance + 1)
                            distances[startValve.name]!![neighbour.name] = distance + 1
                        }
                }
            }


        distances.print()

        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           