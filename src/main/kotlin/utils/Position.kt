package utils

import kotlin.math.abs

fun main() {
    val origin = Position(0, 0)

    origin.get8Neighbours().toList().print()
    //    [Position(x=0, y=1), Position(x=1, y=1), Position(x=1, y=0), Position(x=1, y=-1), Position(x=0, y=-1), Position(x=-1, y=-1), Position(x=-1, y=0), Position(x=-1, y=1)]

    origin.doMovement(Direction4.North).print()
    //    Position(x=0, y=1)

    origin.doMovement(Direction8.NorthEast).print()
    //    Position(x=1, y=1)
}

data class Position(val x: Int, val y: Int) {
    val manhattenDistance = abs(x) + abs(y)

    fun doMovement(direction: Direction4): Position {
        return when (direction) {
            Direction4.North -> Position(x, y + 1)
            Direction4.South -> Position(x, y - 1)
            Direction4.East -> Position(x + 1, y)
            Direction4.West -> Position(x - 1, y)
        }
    }

    fun doMovement(direction: Direction8): Position {
        return when (direction) {
            Direction8.North -> Position(x, y + 1)
            Direction8.NorthEast -> Position(x + 1, y + 1)
            Direction8.East -> Position(x + 1, y)
            Direction8.SouthEast -> Position(x + 1, y - 1)
            Direction8.South -> Position(x, y - 1)
            Direction8.SouthWest -> Position(x - 1, y - 1)
            Direction8.West -> Position(x - 1, y)
            Direction8.NorthWest -> Position(x - 1, y + 1)
        }
    }

    fun get4Neighbours(): Sequence<Position> = sequence {
        Direction4.values().forEach { yield(this@Position.doMovement(it)) }
    }

    fun get8Neighbours(): Sequence<Position> = sequence {
        Direction8.values().forEach { yield(this@Position.doMovement(it)) }
    }
}

enum class Direction4 {
    North, East, South, West
}

enum class Direction8 {
    North, NorthEast, East, SouthEast, South, SouthWest, West, NorthWest
}

