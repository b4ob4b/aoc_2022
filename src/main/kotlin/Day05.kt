import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day05(IO.TYPE.SAMPLE).test("CMZ", "MCD")
    Day05().solve()
}

class Day05(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Supply Stacks", inputType = inputType) {

    private val data = input.split("\n\n").map { it.splitLines() }
    private val commands = data[1].parseCommands()

    private fun List<String>.parseStacks(): List<ArrayDeque<String>> {
        val rows = this.map { row ->
            row.chunked(4).map {
                "[A-Z]".toRegex().find(it)?.value
            }
        }
        val stacks = rows.first().indices.map { ArrayDeque<String>() }
        rows
            .forEach { line ->
                line.forEachIndexed { index, crate ->
                    if (crate != null) stacks[index].add(crate)
                }
            }
        return stacks
    }

    data class Command(val amount: Int, val from: Int, val to: Int)

    private fun List<String>.parseCommands() = this
        .map { it.split(" ").slice(setOf(1, 3, 5)) }
        .map { it.map(String::toInt) }
        .map { (amount, from, to) -> Command(amount, from - 1, to - 1) }

    override fun part1(): String {
        val stacks = data[0].dropLast(1).parseStacks()
        commands.forEach { command ->
            repeat(command.amount) {
                stacks[command.to].addFirst(stacks[command.from].removeFirst())
            }
        }
        return stacks.map { it.first() }.joinToString("")
    }

    override fun part2(): String {
        val stacks = data[0].dropLast(1).parseStacks()
        commands.forEach { command ->
            (0 until command.amount).reversed().forEach {
                stacks[command.to].addFirst(stacks[command.from].removeAt(it))
            }
        }
        return stacks.map { it.first() }.joinToString("")
    }

}
