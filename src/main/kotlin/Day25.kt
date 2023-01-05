import utils.Day
import utils.IO
import utils.splitLines
import kotlin.math.pow

fun main() {
    Day25(IO.TYPE.SAMPLE).test("2=-1=0")
    Day25().solve()
}

class Day25(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Full of Hot Air", inputType = inputType) {

    override fun part1(): String {
        val decimal = input.splitLines().sumOf { it.toDecimal() }
        return Snafu(decimal).rewriteTheSnafuWay().toString()
    }

    override fun part2() = "no part 2"

    private fun String.toDecimal() = this.toList().reversed().mapIndexed { magnitude, c ->
        val number1 = when (c) {
            '-' -> -1
            '=' -> -2
            else -> c.toString().toLong()
        }
        val number2 = (5.0.pow(magnitude).toLong())
        number1 * number2
    }.sum()

    class Snafu(decimal: Long) {

        private val snafu = mutableMapOf<Int, Int>()
        private val base5Numbers = sequence {
            var exponent = 0
            while (true) {
                yield(exponent to 5.0.pow(exponent).toLong())
                exponent++
            }
        }

        init {
            var rest = decimal
            while (rest > 0) {
                val base5Magnitude = base5Numbers.takeWhile { it.second <= rest }.last()
                increment(base5Magnitude.first)
                rest -= base5Magnitude.second
            }
        }

        private fun increment(base5Magnitude: Int) {
            if (snafu.contains(base5Magnitude)) {
                snafu[base5Magnitude] = snafu[base5Magnitude]!! + 1
            } else {
                snafu[base5Magnitude] = 1
            }
        }

        fun rewriteTheSnafuWay(): Snafu {
            for ((magnitude, amount) in snafu.entries.reversed()) {
                if (amount >= 3) {
                    snafu[magnitude] = amount - 5
                    increment(magnitude + 1)
                }
            }
            return this
        }

        override fun toString(): String {
            val highestMagnitude = snafu.keys.max()
            return (highestMagnitude downTo 0).map { magnitude ->
                if (snafu.contains(magnitude)) {
                    when (snafu[magnitude]) {
                        -2 -> "="
                        -1 -> "-"
                        else -> snafu[magnitude].toString()
                    }
                } else {
                    "0"
                }
            }.joinToString("")
        }
    }
}
