package utils

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    val size = 3
    val list = (1..size).toList()

    measureTime {
        list.permuteRecursive()
    }.inWholeMilliseconds.let { "took $it ms to calculate" }.print()

    measureTime {
        list.permutations().toList().print()
        // [[1, 2, 3], [2, 1, 3], [2, 3, 1], [1, 3, 2], [3, 1, 2], [3, 2, 1]]
    }.inWholeMilliseconds.let { "took $it ms to calculate" }.print()

    measureTime {
        list.compositions(size).toList()
    }.inWholeMilliseconds.let { "took $it ms to calculate" }.print()
}


fun <T> List<T>.permuteRecursive(): List<List<T>> {
    if (this.size == 1) return listOf(this)
    val perms = mutableListOf<List<T>>()
    val toInsert = this[0]
    for (perm in this.drop(1).permuteRecursive()) {
        for (i in 0..perm.size) {
            val newPerm = perm.toMutableList()
            newPerm.add(i, toInsert)
            perms.add(newPerm)
        }
    }
    return perms
}

fun <T> List<T>.permutations(): Sequence<List<T>> =
    when (size) {
        0 -> emptySequence()
        1 -> sequenceOf(toList())
        else -> {
            val head = first()
            val tail = drop(1)
            sequence {
                tail.permutations().forEach { perm ->
                    for (i in 0..perm.size) {
                        yield(perm.toMutableList().apply { add(i, head) })
                    }
                }
            }
        }
    }
