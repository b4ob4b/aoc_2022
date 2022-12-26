import utils.*

fun main() {
    Day22(IO.TYPE.SAMPLE).test(6032)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    val map = input.split("\n\n").first()
        .splitLines()
        .map { it.toList() }

    private val tiles = map.flatMapIndexed { y, chars ->
        chars.mapIndexedNotNull { x, char ->
            when (char) {
                '.', '#' -> Position(x, y)
                else -> null
            }
        }
    }

    private val instructions = input.split("\n\n").last().print()
        .let { instructions ->

            val numbers = instructions.split("""R|L""".toRegex()).map { it.toInt() }
            val rotations = instructions.split("""\d+""".toRegex()).filter(String::isNotBlank).map(Rotation::of)

            buildList {
                add(Forward(numbers.first()))
                numbers.drop(1).forEachIndexed { index, number ->
                    add(Turn(rotations[index]))
                    add(Forward(number))
                }
            }
        }

    sealed class Instruction()
    data class Forward(val amount: Int) : Instruction()
    data class Turn(val rotation: Rotation) : Instruction()
    enum class Rotation {
        Left, Right;

        companion object {
            fun of(string: String) = when (string) {
                "R" -> Right
                "L" -> Left
                else -> throw Exception("unknown direction: $string")
            }
        }
    }

    private fun List<List<Char>>.of(position: Position) = this[position.y][position.x]
    
    class VV {
        val map = 1
        class BB(val b: Int=0) {
            fun go() {
                
            }
        }
    }

    data class Santa(val position: Position, val direction: Direction4)

    fun Santa.follow(instruction: Instruction) {
//        when(instruction) {
//            is Forward -> go(instruction.amount)
//        }
    }

    private fun Santa.go(amount: Int) {
        var steps = amount
        while (map.of(position.doMovement(direction)) == '.') {
            steps--
        }
//        return Santa()
    }

    override fun part1(): Any? {
        val start = Santa(tiles.first(), Direction4.East)
        instructions.print()

//        instructions.fold(start) {santa, instruction ->  
//            santa.follow(instruction)
//        }

        map.print()
        tiles.map { map.of(it) }.print()
        return "not yet implement"
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           