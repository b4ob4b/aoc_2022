package utils

fun main() {
    listOf(1,2,3,1,2,1).allIndicesOf(1).toList().print()
    //    [0, 3, 5]
}

fun <T> Iterable<T>.allIndicesOf(element: T) = sequence {
    var index = 0
    this@allIndicesOf.iterator().forEach {
        if(it == element) yield(index)
        index++
    }
}

fun Collection<Int>.product() = this.reduce { acc, i -> acc * i }