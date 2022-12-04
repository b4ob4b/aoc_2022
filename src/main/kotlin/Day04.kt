import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day04(IO.TYPE.SAMPLE).test(2,4)
    Day04().solve()
}

class Day04(inputType: IO.TYPE = IO.TYPE.INPUT) : Day(inputType = inputType) {
    override fun part1(): Int {
        return input.splitLines()
            .map {
                it.split(",")
                    .map {
                        it.split("-").map { it.toInt() }
                    }
                    .map { (it[0]..it[1]).toSet() }
            }.count { sets -> sets[0].containsAll(sets[1]) || sets[1].containsAll(sets[0]) }
    }

    override fun part2(): Int {
        return input.splitLines()
            .map {
                it.split(",")
                    .map {
                        it.split("-").map { it.toInt() }
                    }
                    .map { (it[0]..it[1]).toSet() }
            }.count { sets -> sets[0].fold(false) { acc, i -> acc || sets[1].contains(i) } }
    }
}           