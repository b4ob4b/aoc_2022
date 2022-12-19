import utils.*
import java.lang.Integer.max

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

    override fun part1(): Int {
        val states = valves.associate { it.name to State.Closed }
        return dfs(30, "AA", states)
    }

    override fun part2(): Any? {
        return "not yet implement"
    }

    data class Valve(
        val name: String,
        val rate: Int,
        val tunnels: List<String>,
    )

    enum class State { Open, Closed }

    private fun dfs(
        time: Int,
        position: String,
        states: Map<String, State>,
    ): Int {
        var maxFlow = 0

        val states = states.toMutableMap()
        val closedValves = distances[position]!!.filter { (name, _) -> states[name] == State.Closed }
        for ((name, distance) in closedValves) {
            val valve = valves.single { it.name == name }
            val remainingTime = time - distance - 1
            if (remainingTime <= 0) continue
            states[name] = State.Open
            maxFlow = max(maxFlow, dfs(remainingTime, valve.name, states) + remainingTime * valve.rate)
        }
        return maxFlow
    }
}           