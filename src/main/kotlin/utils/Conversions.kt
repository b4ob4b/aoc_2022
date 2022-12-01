package utils

fun <T> T.print() = println(this)

fun String.splitLines() = split("\n")

fun String.toBinary() = Integer.parseInt(this, 2)

fun String.extractInts(separator: String = ",") = this.split(separator).map { it.toInt() }