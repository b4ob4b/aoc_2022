package utils

fun main() {
    listOf(1, 2, 3).combinations(2).toList().print()
    // [[1, 2], [1, 3], [2, 3]]

    setOf(1, 2, 3).compositions(2).toList().print()
    // [[1, 2], [1, 3], [2, 1], [2, 3], [3, 1], [3, 2]]

}

/*
* creates combinations of length n of given elements of collection
* */
fun <T> Collection<T>.combinations(n: Int): Sequence<List<T>> {
    return when (n) {
        0 -> emptySequence()
        1 -> this@combinations.asSequence().map { listOf(it) }
        else -> sequence {
            this@combinations.forEachIndexed { index, i ->
                this@combinations.combinations(n - 1).drop(index + 1).forEach {
                    yield(listOf(i) + it)
                }
            }
        }
    }
}

/*
* creates different compositions of given size of all elements in list
* */
fun <T> Collection<T>.compositions(size: Int): Sequence<List<T>> {
    return when (size) {
        0 -> emptySequence()
        1 -> this.asSequence().map { listOf(it) }
        else -> sequence {
            this@compositions.forEachIndexed { index, t ->
                val rest = this@compositions.toMutableList()
                rest.removeAt(index)
                rest.compositions(size - 1).forEach {
                    yield(listOf(t) + it)
                }
            }
        }
    }
}
