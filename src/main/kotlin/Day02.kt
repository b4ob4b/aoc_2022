import utils.Day
import utils.IO
import utils.print
import utils.splitLines

fun main() {
    Day02().solve()
}

class Day02: Day(inputType = IO.TYPE.INPUT) {

    val map = mapOf(
        Pair(Pair("A","X"),1+3),
        Pair(Pair("A","Y"),2+6),
        Pair(Pair("A","Z"),3+0),
        Pair(Pair("B","X"),1+0),
        Pair(Pair("B","Y"),2+3),
        Pair(Pair("B","Z"),3+6),
        Pair(Pair("C","X"),1+6),
        Pair(Pair("C","Y"),2+0),
        Pair(Pair("C","Z"),3+3)
    )
// X losa
//    Y draw
//    Z win
    val map2 = mapOf(
//                  object,  losw
        Pair(Pair("A","X"),3+0),
        Pair(Pair("A","Y"),1+3),
        Pair(Pair("A","Z"),2+6),
        Pair(Pair("B","X"),1+0),
        Pair(Pair("B","Y"),2+3),
        Pair(Pair("B","Z"),3+6),
        Pair(Pair("C","X"),2+0),
        Pair(Pair("C","Y"),3+3),
        Pair(Pair("C","Z"),1+6)
    )

    val data = input.splitLines()
        .map { Pair(it.split(" ").first(), it.split(" ")[1]) }

    override fun part1(): Any? {
       return  data.map {
            map.get(it)
        }.sumOf { it!! }
        return "not yet implement"
    }


    override fun part2(): Any? {
        return  data.map {
            map2.get(it)
        }.sumOf { it!! }
        return "not yet implement"
    }
}           