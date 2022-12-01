import utils.*

fun main() {
    Day01().solve()
}

class Day01: Day(inputType = IO.TYPE.INPUT) {

    private val calories = input.split("\n\n")
        .map { it.split("\n").map { it.toInt() } }
        .map { it.sum() }

    override fun part1(): Int {
        return calories
            .max()
    }

    override fun part2(): Int {
        return calories
            .sortedDescending()
            .take(3)
            .sum()
    }
}