package utils

import java.math.BigDecimal
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@OptIn(ExperimentalTime::class)
fun main() {
    measureTime {
        fib(100).print()
    }.inWholeMilliseconds.let { "took $it ms to calculate" }.print()
    //    354224848179261915075
    //    took 4 ms to calculate
}

/*
* optimized fibonacci recursion with memo
* */
fun fib(n: Int, memo: HashMap<Int, BigDecimal> = hashMapOf()): BigDecimal {
    if (n in memo.keys) return memo[n]!!
    if (n <= 2) return BigDecimal(1)
    memo[n] = fib(n - 2, memo) + fib(n - 1, memo)
    return memo[n]!!
}