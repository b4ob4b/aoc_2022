import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day02().solve()
}

class Day02: Day(inputType = IO.TYPE.INPUT) {

    private val scoresGame1 = mapOf(
        Pair('A','X') to 1+3,
        Pair('A','Y') to 2+6,
        Pair('A','Z') to 3+0,
        Pair('B','X') to 1+0,
        Pair('B','Y') to 2+3,
        Pair('B','Z') to 3+6,
        Pair('C','X') to 1+6,
        Pair('C','Y') to 2+0,
        Pair('C','Z') to 3+3
    )

    private val scoresGame2 = mapOf(
        Pair('A','X') to 3+0,
        Pair('A','Y') to 1+3,
        Pair('A','Z') to 2+6,
        Pair('B','X') to 1+0,
        Pair('B','Y') to 2+3,
        Pair('B','Z') to 3+6,
        Pair('C','X') to 2+0,
        Pair('C','Y') to 3+3,
        Pair('C','Z') to 1+6
    )

    val games = input.splitLines()
        .map { it.split(" ").map(String::first) }
        .map { (opponent,me) -> Pair(opponent, me) }

    override fun part1(): Int {
       return games.mapNotNull(scoresGame1::get).sum()
    }

    override fun part2(): Int {
        return games.mapNotNull(scoresGame2::get).sum()
    }
}           