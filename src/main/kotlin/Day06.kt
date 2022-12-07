import utils.Day
import utils.IO

fun main() {
    Day06(IO.TYPE.SAMPLE).test(7,19)
    Day06().solve()
}

class Day06(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Tuning Trouble", inputType = inputType) {

    private val signal = input.asSequence()
    
    private fun Sequence<Char>.calculateSignalStart(startOfPacketSize: Int) = this
        .windowed(startOfPacketSize)
        .indexOfFirst { it.toSet().size == startOfPacketSize }
        .let { it + startOfPacketSize }

    override fun part1(): Int {
        return signal.calculateSignalStart(4)
    }

    override fun part2(): Int {
        return signal.calculateSignalStart(14)
    }
}           