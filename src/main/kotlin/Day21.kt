import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day21(IO.TYPE.SAMPLE).test(152L, 301L)
    Day21().solve()
}

class Day21(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Monkey Math", inputType = inputType) {


    override fun part1() = MonkeyMath(input).solveFor("root")

    override fun part2() = MonkeyMath(input).equalAt("root").transposeFor("humn").solveFor("humn")

    class MonkeyMath(val input: String) {

        private val monkeys = input.splitLines()
            .map {
                val words = it.split(" ")
                val name = words[0].dropLast(1)
                if (words.size == 2) {
                    Monkey(name, result = words[1].toLong())
                } else {
                    Monkey(name, words[1], words.last(), Operation.of(words[2]))
                }
            }.toMutableList()

        private fun List<Monkey>.byName(name: String?) = this.single { it.name == name }

        fun solveFor(monkeyName: String): Long {
            val monkey = monkeys.byName(monkeyName)
            while (monkey.result == null) {
                monkeys.filter { it.result == null }
                    .forEach { monkey ->
                        val operand1 = monkeys.byName(monkey.operand1).result
                        val operand2 = monkeys.byName(monkey.operand2).result
                        if (operand1 != null && operand2 != null) {
                            monkey.result = monkey.operation?.on(operand1, operand2)
                        }
                    }
            }
            return monkey.result!!
        }

        fun equalAt(string: String): MonkeyMath {
            val equalityMonkey = monkeys.byName(string)
            val equal = solveFor(equalityMonkey.operand2!!)
            monkeys.reset()
            monkeys.add(Monkey(equalityMonkey.operand1!!, result = equal))
            return this
        }

        fun transposeFor(string: String): MonkeyMath {
            monkeys.remove(monkeys.byName(string))

            val queue = ArrayDeque<String>()
            queue.add(string)

            val monkeysTransposed = mutableListOf<Monkey>()

            while (queue.isNotEmpty()) {
                val monkeyName = queue.removeFirst()
                if (monkeys.firstOrNull { it.name == monkeyName }?.result != null) continue

                monkeys
                    .filter { it.operand1 == monkeyName || it.operand2 == monkeyName }
                    .filter { it.result == null }
                    .forEach { monkey ->
                        val newMonkey = monkey.transposeFor(monkeyName)
                        monkeysTransposed.add(newMonkey)
                        monkeys.remove(monkey)
                        queue.add(newMonkey.operand1!!)
                        queue.add(newMonkey.operand2!!)
                    }
            }

            monkeys.addAll(monkeysTransposed)
            return this
        }


        private fun List<Monkey>.reset() = this.forEach { monkey ->
            if (monkey.operation != null) monkey.result = null
        }

        data class Monkey(
            val name: String,
            val operand1: String? = null,
            val operand2: String? = null,
            val operation: Operation? = null,
            var result: Long? = null
        ) {
            fun transposeFor(monkeyName: String): Monkey {
                return when (monkeyName) {
                    operand1 -> Monkey(monkeyName, name, operand2, operation!!.opposite)
                    else -> when (operation) {
                        Operation.Plus, Operation.Times -> Monkey(monkeyName, name, operand1, operation.opposite)
                        else -> Monkey(monkeyName, operand1, name, operation)
                    }
                }
            }
        }
    }


    enum class Operation {
        Plus,
        Minus,
        Times,
        DivededBy;

        fun on(operand1: Long, operand2: Long) = when (this) {
            Plus -> operand1 + operand2
            Minus -> operand1 - operand2
            Times -> operand1 * operand2
            DivededBy -> operand1 / operand2
        }

        val opposite
            get() = when (this) {
                Plus -> Minus
                Minus -> Plus
                Times -> DivededBy
                DivededBy -> Times
            }

        companion object {
            fun of(string: String) = when (string) {
                "+" -> Plus
                "-" -> Minus
                "*" -> Times
                "/" -> DivededBy
                else -> throw Exception("unknown operation: $string")
            }
        }
    }
}
