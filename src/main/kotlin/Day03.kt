import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day03(IO.TYPE.SAMPLE).test(157,70)
    Day03().solve()
}

class Day03(inputType: IO.TYPE = IO.TYPE.INPUT): Day(inputType = inputType) {
    override fun part1(): Int {
        return input.splitLines().map { it.toCharArray().asList() }
            .map { Pair(it.subList(0, it.size / 2).toSet(),it.subList(it.size / 2, it.size).toSet()) }
            .map { it.first.intersect(it.second) }
            .map { it.single().toInt() - 96 }
            .map { if(it < 0) it + 58 else it }
            .sum()
    }

    override fun part2(): Int {
        return input.splitLines().chunked(3).map {
            it.map { it.toCharArray() }
                .reduce { acc, chars -> acc.intersect(chars.toList()).toCharArray() }
                .map { it.toString().toCharArray().single().toInt()  - 96 }
                .map { if(it < 0) it + 58 else it }
        }.flatten().sum()
    }
}           