import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day05(IO.TYPE.SAMPLE).test("CMZ", "MCD")
    Day05().solve()
}

class Day05(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    val groups = input.split("\n\n")
    val first = groups[0].splitLines()
    val horizontalLenght = first.map { it.length }.max()
    val stacksRaw = first.dropLast(1)
        .map {
            val row = it.split("")
            if (row.size < horizontalLenght) {
                row + (0..(horizontalLenght - row.size)).toList().map { " " }
            } else row
        }
    val stackPositions = (0..10).map { ((it + 0.5) * 4).toInt() }.filter { it < horizontalLenght }

    val stacksRaw2 = stacksRaw.map { it.slice(stackPositions) }

    private fun getStacks(): List<ArrayDeque<String>> {
        val stacks = stackPositions.indices.map { ArrayDeque<String>() }

        for (height in stacksRaw2.indices) {
            for (stackNumber in stackPositions.indices) {
                val crate = stacksRaw2[height][stackNumber]
                if (crate != " ") stacks[stackNumber].addFirst(crate)
            }
        }
        return stacks
    }


    val commands = groups[1].splitLines()
        .map { it.split(" ") }
        .map { it.slice(setOf(1, 3, 5)) }
        .map { it.map { it.toInt() } }

    override fun part1(): Any? {
        val stacks = getStacks()
        for (command in commands) {
            val amount = command[0]
            val from = command[1] - 1
            val to = command[2] - 1

            for (step in (1..amount)) {
                val crate = stacks[from].removeLast()

                stacks[to].add(crate)
            }
        }
        return stacks.map { it.last() }.joinToString("")
    }

    override fun part2(): Any? {
        val stacks = getStacks()
        for (command in commands) {
            val amount = command[0]
            val from = command[1] - 1
            val to = command[2] - 1

            val crate = ArrayDeque<String>()
            for (step in (1..amount)) {
                crate.add(stacks[from].removeLast())
            }
            for (step in (1..amount)) {
                stacks[to].add(crate.removeLast())
            }
        }
        return stacks.map { it.last() }.joinToString("")
    }


}