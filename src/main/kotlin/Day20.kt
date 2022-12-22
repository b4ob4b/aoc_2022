import utils.*

fun main() {
    Day20(IO.TYPE.SAMPLE).test(3, 1623178306L)
    Day20().solve()
}

class Day20(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val groovePositions = setOf(1000, 2000, 3000)
    private val decryptionKey = 811589153L

    override fun part1() = Ring(input).spin().getGroveCoordinates(groovePositions).sum().toInt()

    override fun part2(): Long {
        val ring = Ring(input)
        repeat(10) {
            ring.spin(decryptionKey)
        }
        return ring.getGroveCoordinates(groovePositions).sumOf { it * decryptionKey }
    }

    class Ring(input: String) {

        private val ring = ArrayDeque<Pair<Int, Long>>()
        private val size = input.splitLines().size
        private var current = 0

        init {
            input.splitLinesToInt().forEachIndexed { index, i ->
                ring.add(index to i.toLong())
            }
        }

        fun spin(decryptionKey: Long = 1): Ring {
            repeat(ring.size) {
                shift(decryptionKey)
            }
            return this
        }

        private fun Long.transformToPositive(decryptionKey: Long): Int {
            var value = this * decryptionKey
            while (value < 0) {
                value += ring.size * decryptionKey
            }
            return (value % ring.size).toInt()
        }

        private fun shift(decryptionKey: Long): Ring {
            do {
                val (index, value) = ring.removeFirst()
                ring.addLast(index to value)
            } while (index != (current % ring.size))
            val (index, value) = ring.removeLast()

            val position = when {
                value < 0 -> value.transformToPositive(decryptionKey)
                else -> ((value * decryptionKey) % ring.size).toInt()
            }
            ring.add(position, index to value)
            current++
            return this
        }

        fun getGroveCoordinates(positions: Set<Int>): List<Long> {
            val numbers = ring.map { it.second }
            val zeroPosition = numbers.indexOf(0)
            return positions.map { numbers[(zeroPosition + it) % size] }
        }
    }
}


           