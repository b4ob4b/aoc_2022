import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day04(IO.TYPE.SAMPLE).test(2, 4)
    Day04().solve()
}

class Day04(inputType: IO.TYPE = IO.TYPE.INPUT) : Day(inputType = inputType) {

    private val sections = input.splitLines().parseSections()

    private fun List<String>.parseSections() = this
        .map { it.split(Regex(",|-")).map(String::toInt) }
        .map { (a, b, c, d) -> Pair(a..b, c..d) }

    private infix fun IntRange.containsAll(other: IntRange) = other.first >= this.first && other.last <= this.last

    override fun part1() = sections.count { (range1, range2) ->
        range1 containsAll range2 || range2 containsAll range1
    }

    override fun part2() = sections.count { (range1, range2) -> (range1 intersect range2).isNotEmpty() }
}           