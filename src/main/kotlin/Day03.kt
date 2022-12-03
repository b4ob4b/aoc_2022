import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day03(IO.TYPE.SAMPLE).test(157, 70)
    Day03().solve()
}

class Day03(inputType: IO.TYPE = IO.TYPE.INPUT) : Day(inputType = inputType) {

    private val rucksacks = input.splitLines()
    private fun Char.calculatePriority() = (('a'..'z') + ('A'..'Z')).indexOf(this) + 1

    override fun part1(): Int {
        return rucksacks.asSequence().map { it.toList() }
            .map { it.chunked(it.size / 2).map { it.toSet() } }
            .map { (compartment1, compartment2) -> compartment1.intersect(compartment2) }
            .map { it.single().calculatePriority() }
            .sum()
    }

    override fun part2(): Int {
        return rucksacks.chunked(3).flatMap { group ->
            group.map { it.toCharArray().toSet() }
                .reduce { acc, chars -> acc.intersect(chars) }
                .map { it.calculatePriority() }
        }.sum()
    }
}           