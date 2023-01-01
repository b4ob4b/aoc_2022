package utils.matrix

import utils.navigation.Direction4

data class MatrixPoint(val row: Int, val col: Int) {
    fun moveTo(direction: Direction4): MatrixPoint {
        return when (direction) {
            Direction4.North -> MatrixPoint(row - 1, col)
            Direction4.South -> MatrixPoint(row + 1, col)
            Direction4.East -> MatrixPoint(row, col + 1)
            Direction4.West -> MatrixPoint(row, col - 1)
        }
    }
}