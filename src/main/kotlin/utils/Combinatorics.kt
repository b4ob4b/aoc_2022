package utils

fun List<Int>.combinations(n: Int): Sequence<List<Int>> {
 return when(n) {
  0 -> emptySequence()
  1 -> this@combinations.asSequence().map { listOf(it) }
  else -> sequence {
   this@combinations.forEachIndexed { index, i ->
    this@combinations.combinations(n-1).drop(index + 1).forEach {
     yield(listOf(i) + it)
    }
   }
  }
 }
}