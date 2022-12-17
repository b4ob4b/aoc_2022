import utils.*

fun main() {
    Day13(IO.TYPE.SAMPLE).test(13, 140)
    Day13().solve()
}

class Day13(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Distress Signal", inputType = inputType) {

    private val pairs = input
        .split("\n\n")
        .flatMap { it.splitLines().zipWithNext() }
        .map { (left, right) -> Packet.from(left) to Packet.from(right) }

    private val dividerPackets = listOf(
        Packet.from("[[2]]"),
        Packet.from("[[6]]"),
    )

    override fun part1(): Int {
        return pairs
            .map { (left, right) -> left compareTo right }
            .mapIndexedNotNull { index, result -> if (result != 1) index + 1 else null }
            .sum()
    }

    override fun part2(): Int {
        return pairs
            .flatMap { listOf(it.first, it.second) }
            .let { it + dividerPackets }
            .sorted()
            .mapIndexedNotNull { index, packet ->
                if (dividerPackets.contains(packet)) index + 1 else null
            }.product()
    }

    sealed class Value() : Comparable<Value> {
        abstract override infix fun compareTo(other: Value): Int

        companion object {
            fun from(information: String): Value {
                if (information.contains("""\[|]""".toRegex())) {
                    return Packet.from(information)
                } else {
                    val number = information.toIntOrNull()
                    return when (number) {
                        null -> Packet.from(information)
                        else -> Number(number)
                    }
                }
            }
        }
    }

    data class Number(val value: Int) : Value() {
        override fun compareTo(other: Value): Int {
            when (other) {
                is Number -> {
                    return when {
                        value < other.value -> -1
                        value == other.value -> 0
                        else -> 1
                    }
                }
                is Packet -> {
                    return Packet(listOf(this)) compareTo other
                }
            }
        }
    }

    data class Packet(val children: List<Value>) : Value() {
        override fun compareTo(other: Value): Int {
            when (other) {
                is Number -> {
                    return this compareTo Packet(listOf(other))
                }
                is Packet -> {
                    var i = 0
                    while (i < children.size && i < other.children.size) {
                        val r = children[i] compareTo other.children[i]
                        if (r == -1) return -1
                        if (r == 1) return 1
                        i++
                    }
                    return if (i == children.size && i < other.children.size) -1
                    else if (i == other.children.size && i < children.size) 1
                    else 0
                }
            }
        }

        companion object {
            fun from(information: String): Packet {
                if (information.isEmpty()) return Packet(emptyList())
                var current = ""
                var brackets = 0
                val children = buildList {
                    for (char in information.drop(1).dropLast(1)) {
                        if (char == '[') brackets++
                        if (char == ']') brackets--
                        if (char == ',' && brackets == 0) {
                            add(Value.from(current))
                            current = ""
                            continue
                        }
                        current += char
                    }
                    add(Value.from(current))
                }

                return Packet(children)
            }
        }
    }
}           