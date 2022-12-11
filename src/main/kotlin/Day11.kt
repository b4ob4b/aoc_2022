import utils.*

fun main() {
    Day11(IO.TYPE.SAMPLE).test(10605, 2713310158)
    Day11().solve()
}

class Day11(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Monkey in the Middle", inputType = inputType) {

    private fun String.parseMonkeys() = this.split("\n\n")
        .map {
            val monkey = it.splitLines().drop(1)
            val items = monkey[0].split("Starting items: ")[1].split(", ").map(String::toLong)
            val operation = monkey[1].split("Operation: new = ")[1]
            val divisibleBy = monkey[2].split("Test: divisible by ")[1].toInt()
            val ifTrue = monkey[3].split("If true: throw to monkey ")[1].toInt()
            val ifFalse = monkey[4].split("If false: throw to monkey ")[1].toInt()
            Monkey(items.toMutableList(), operation, divisibleBy, ifTrue, ifFalse)
        }
    
    override fun part1(): Int {
        val monkeys = input.parseMonkeys()
        playMonkeyRounds(20, monkeys, 3)
        return monkeys.calculateMonkeyBusinessLevel().toInt()
    }

    override fun part2(): Long {
        val monkeys = input.parseMonkeys()
        playMonkeyRounds(10000, monkeys)
        return monkeys.calculateMonkeyBusinessLevel()
    }

    data class Monkey(
        private val items: MutableList<Long>,
        private val operation: String,
        val divisibleBy: Int,
        private val ifTrue: Int,
        private val ifFalse: Int
    ) {
        var numberOfInspectations = 0

        fun hasItems() = items.isNotEmpty()

        fun inspectAndThrow(worryLevelDivider: Int, modulo: Int? = null): Pair<Int, Long> {
            numberOfInspectations++
            if (items.isEmpty()) return -1 to 0
            val item = items.removeAt(0).let {
                if (modulo != null) it % modulo else it
            }
            val worryLevel = (operation.applyOperation(item) / worryLevelDivider)
            val throwToMonkey = if (worryLevel % divisibleBy == 0L) ifTrue else ifFalse
            return throwToMonkey to worryLevel
        }

        fun catch(item: Long) {
            items.add(item)
        }

        private fun String.applyOperation(old: Long): Long {
            val (_, operation, op2) = this.split(" ")
            val operand1 = old
            val operand2 = if (op2 == "old") old else op2.toLong()
            return when (operation) {
                "+" -> operand1 + operand2
                "*" -> operand1 * operand2
                else -> 0
            }
        }
    }

    private fun List<Monkey>.calculateMonkeyBusinessLevel() = this
        .map {
            it.numberOfInspectations.toLong()
        }.sortedDescending().take(2).product()

    private fun playMonkeyRounds(rounds: Int, monkeys: List<Monkey>, worryLevelDivider: Int = 1) {
        val lcm = monkeys.map { it.divisibleBy }.product()
        repeat(rounds) {
            monkeys.forEach { monkey ->
                while (monkey.hasItems()) {
                    val (monkeyId, item) = monkey.inspectAndThrow(worryLevelDivider, lcm)
                    monkeys[monkeyId].catch(item)
                }
            }
        }
    }
}

           