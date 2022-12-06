import utils.Day
import utils.IO
import utils.print

fun main() {
    Day06(IO.TYPE.SAMPLE).test(7,19)
    Day06().solve()
}

class Day06(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {
    
    override fun part1(): Any? {
        return input.toCharArray().toList().windowed(4).map { it.toSet() }
            .mapIndexed { index, chars -> 
            if (chars.size == 4) index else null
        }
            .filterNotNull()
            .first()
            .let { it+4 }
    }

    override fun part2(): Any? {
        return input.toCharArray().toList().windowed(14).map { it.toSet() }
            .also { it.print() }
            .also { it.map { it.size }.print() }
            .mapIndexed { index, chars ->
                if (chars.size == 14) index else null
            }
            .filterNotNull()
            .first()
            .let { it+14 }
    }
}           