import utils.*

fun main() {
    Day16(IO.TYPE.SAMPLE).test(1651)
    Day16().solve()
}

class Day16(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val valves = input.splitLines().map {
        it.split(" ").let { information ->
            Valve(information[1],
                information[4].split("""=|;""".toRegex())[1].toInt(),
                information.slice(9 until information.size).map { it.removeSuffix(",") })
        }
    }

    private val distances: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

    init {
        valves.filter { it.rate > 0 || it.name == "AA" }.`forEach` { startValve ->

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

    data class FlowManagement(val timeLeft: Int, val position: String, val opened: List<String> = emptyList(), val pressure: Int = 0)

    override fun part1(): Int {
        val queue = ArrayDeque<FlowManagement>()
        queue.add(FlowManagement(30, "AA"))

        var maxPressure = 0

        while (queue.isNotEmpty()) {

            val (time, position, opened, pressure) = queue.removeFirst()

            if (pressure > maxPressure) maxPressure = pressure

            if (time == 0) continue

            val neighbours = distances[position]!!
            neighbours
                .filter { it.key !in opened }
                .forEach { (valve, distance) ->

                    val timeLeft = time - distance - 1
                    val additionalPressure = valves.single { it.name == valve }.rate * if (timeLeft == 0) 1 else timeLeft

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

        return maxPressure
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    data class Valve(
        val name: String,
        val rate: Int,
        val tunnels: List<String>,
    )

}           