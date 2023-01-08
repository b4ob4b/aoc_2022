import utils.*

fun main() {
    Day16(IO.TYPE.SAMPLE).test(1651)
    Day16().solve()
}

class Day16(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Proboscidea Volcanium", inputType = inputType) {

    private val valves = input.parseValves()
    private val distances: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    init {
        measureTunnelDistances()
    }

    override fun part1() = bfs(30, distances).pressure

    override fun part2(): Int {
        val time = 26

        val person1 = bfs(time, distances, false)
        val closedValves =
            distances.map { (valve, distances) ->
                valve to distances.filter { it.key !in person1.openedValves }
            }.toMap()
        val elephant = bfs(time, closedValves, false)

        return person1.pressure + elephant.pressure
    }

    data class FlowManagement(val timeLeft: Int, val position: String, val opened: List<String> = emptyList(), val pressure: Int = 0)

    data class Flow(val pressure: Int, val openedValves: List<String>)

    data class Valve(
        val name: String,
        val rate: Int,
        val tunnels: List<String>,
    )

    private fun bfs(initialTime: Int, distances: Map<String, Map<String, Int>>, part1: Boolean = true): Flow {
        val queue = ArrayDeque<FlowManagement>()
        queue.add(FlowManagement(initialTime, "AA"))

        var maxPressure = 0
        var visitedValves: List<String> = emptyList()

        while (queue.isNotEmpty()) {

            val (time, position, opened, pressure) = queue.removeFirst()

            if (pressure > maxPressure) {
                maxPressure = pressure
                visitedValves = opened
            }

            if (time == 0) continue

            val neighbours = distances[position]!!
            neighbours
                .filter { it.key !in opened }
                .forEach { (valve, distance) ->

                    val timeLeft = time - distance - 1
                    val additionalPressure = valves.single { it.name == valve }.rate * if (part1 && timeLeft == 0) 1 else timeLeft

                    if (timeLeft >= 0) {
                        queue.add(
                            FlowManagement(
                                timeLeft,
                                valve,
                                opened + valve,
                                pressure + additionalPressure
                            )
                        )
                    }
                }
        }

        return Flow(maxPressure, visitedValves)
    }

    private fun measureTunnelDistances() {
        valves.filter { it.rate > 0 || it.name == "AA" }.forEach { startValve ->

            distances[startValve.name] = mutableMapOf()

            val queue = ArrayDeque<Pair<Valve, Int>>()
            queue.add(startValve to 0)

            val visited = mutableSetOf<Valve>()
            while (queue.isNotEmpty()) {
                val (valve, distance) = queue.removeFirst()
                visited.add(valve)
                valve.tunnels.map { name -> valves.single { it.name == name } }.filter { it !in visited }.forEach { neighbour ->
                    queue.add(neighbour to distance + 1)
                    if (neighbour.rate > 0) distances[startValve.name]!![neighbour.name] = distance + 1
                }
            }
        }
    }

    private fun String.parseValves() = this.splitLines().map {
        it.split(" ").let { information ->
            Valve(information[1],
                information[4].split("""=|;""".toRegex())[1].toInt(),
                information.slice(9 until information.size).map { it.removeSuffix(",") })
        }
    }

}           