import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day21(IO.TYPE.SAMPLE).test(152L, 301L)
    Day21().solve()
}

class Day21(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    data class Monkey(val name: String, var result: Long? = null, val op1: String? = null, val op2: String? = null, val op: String? = null)

    private fun getMokeys() = input.splitLines()
        .map {
            val words = it.split(" ")
            val name = words[0].dropLast(1)
            if (words.size == 2) {
                Monkey(name, words[1].toLong())
            } else {
                Monkey(name, op1 = words[1], op2 = words.last(), op = words[2])
            }
        }

    private fun String.doOperation(op1: Long, op2: Long) = when (this) {
        "+" -> op1 + op2
        "-" -> op1 - op2
        "*" -> op1 * op2
        else -> op1 / op2
    }

    private fun List<Monkey>.byName(name: String) = this.single { it.name == name }


    override fun part1(): Long {
        val monkeys = getMokeys()
        val root = monkeys.byName("root")

        while (root.result == null) {
            monkeys.filter { it.result == null }.forEach { monkey ->
                val op1 = monkeys.byName(monkey.op1!!).result
                val op2 = monkeys.byName(monkey.op2!!).result
                if (op1 != null && op2 != null) {
                    monkey.result = monkey.op!!.doOperation(op1, op2)
                }
            }
        }
        return root.result as Long
    }

    private fun String.opposite() = when (this) {
        "+" -> "-"
        "-" -> "+"
        "*" -> "/"
        "/" -> "*"
        else -> throw Exception("unknown operation")
    }

    override fun part2(): Any? {
        var monkeys = getMokeys().toMutableList()
        val root = monkeys.byName("root")
        val p2 = monkeys.byName(root.op2!!)
        while (p2.result == null) {
            monkeys.filter { it.result == null }.forEach { monkey ->
                val op1 = monkeys.byName(monkey.op1!!).result
                val op2 = monkeys.byName(monkey.op2!!).result
                if (op1 != null && op2 != null) {
                    monkey.result = monkey.op!!.doOperation(op1, op2)
                }
            }
        }
        monkeys = getMokeys().toMutableList()
//        println()
//        p2.print()
        monkeys.remove(root)
        monkeys.remove(p2)
        monkeys.remove(monkeys.byName("humn"))
//        monkeys.add(Monkey(root.op1!!, result = p2.result))

//        monkeys.forEach { it.print() }
        val queue = ArrayDeque<String>()
        queue.add("humn")

        val monkeysTransposed = mutableListOf<Monkey>()

        while (queue.isNotEmpty()) {
            val monkeyName = queue.removeFirst()
            if (monkeys.firstOrNull { it.name == monkeyName }?.result != null) continue

            monkeys
                .filter { it.op1 == monkeyName || it.op2 == monkeyName }
                .filter { it.result == null }
                .forEach { monkey ->
                    val newMonkey = if (monkey.op in listOf("+", "*")) {
                        if (monkeyName == monkey.op2) {
                            Monkey(monkeyName, null, monkey.name, monkey.op1, monkey.op!!.opposite())
                        } else {
                            Monkey(monkeyName, null, monkey.name, monkey.op2, monkey.op!!.opposite())
                        }
                    } else {
                        if (monkeyName == monkey.op2) {
                            Monkey(monkeyName, null, monkey.op1, monkey.name, monkey.op)
                        } else {
                            Monkey(monkeyName, null, monkey.name, monkey.op2, monkey.op!!.opposite())
                        }
                    }
                    monkeysTransposed.add(newMonkey)
                    monkeys.remove(monkey)
                    queue.add(newMonkey.op1!!)
                    queue.add(newMonkey.op2!!)
                }
        }

        monkeys.addAll(monkeysTransposed)
        monkeys.add(Monkey(root.op1!!, result = p2.result))

        val humn = monkeys.byName("humn")
        while (humn.result == null) {
            monkeys.filter { it.result == null }.forEach { monkey ->
                val op1 = monkeys.byName(monkey.op1!!).result
                val op2 = monkeys.byName(monkey.op2!!).result
                if (op1 != null && op2 != null) {
                    monkey.result = monkey.op!!.doOperation(op1, op2)
                }
            }
        }

//        3759566892641

        return humn.result
    }
}