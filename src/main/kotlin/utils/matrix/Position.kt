package utils.matrix

import utils.navigation.Direction4

data class Position(val row: Int, val col: Int) {
    fun moveTo(direction: Direction4): Position {
        return when (direction) {
            Direction4.North -> Position(row - 1, col)
            Direction4.South -> Position(row + 1, col)
            Direction4.East -> Position(row, col + 1)
            Direction4.West -> Position(row, col - 1)
        }
    }
}