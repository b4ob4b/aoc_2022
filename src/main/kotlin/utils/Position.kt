package utils

import kotlin.math.abs

fun main() {
    val origin = Position(0, 0)

    origin.getNeighbours().toList().print()
    //    [Position(x=-1, y=-1), Position(x=-1, y=0), Position(x=-1, y=1), Position(x=0, y=-1), Position(x=0, y=1), Position(x=1, y=-1), Position(x=1, y=0), Position(x=1, y=1)]

    origin.doMovement(Direction.up).print()
    //    Position(x=0, y=1)
}

data class Position(val x: Int, val y: Int) {
    fun getManhattenDistance() = abs(x) + abs(y)

    fun doMovement(direction: Direction): Position {
        return when (direction) {
            Direction.up -> Position(x, y + 1)
            Direction.down -> Position(x, y - 1)
            Direction.right -> Position(x + 1, y)
            Direction.left -> Position(x - 1, y)
        }
    }

    fun getNeighbours(): Sequence<Position> = sequence {
        for (dx in (x - 1)..(x + 1)) {
            for (dy in (y - 1)..(y + 1)) {
                if (dx == x && dy == y) continue
                yield(Position(dx, dy))
            }
        }
    }
}

enum class Direction {
    up, right, down, left

}

