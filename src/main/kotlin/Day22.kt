import utils.Day
import utils.IO
import utils.Position
import utils.matrix.Matrix
import utils.matrix.MatrixPoint
import utils.navigation.Direction4.*
import utils.navigation.Direction4
import utils.navigation.Rotation
import utils.print
import utils.splitLines
import utils.toMatrix

fun main() {
    Day22(IO.TYPE.SAMPLE).test(6032)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    val map = Map(input)

    private val instructions = input.parseInstructions()

    override fun part1(): Int {
        val start = Santa(map.positions.first(), East)

        val finalSanta = instructions.fold(start) { santa, instruction ->
            santa follow instruction
        }

        return calculateFinalPassword(finalSanta)
    }

    override fun part2(): Any? {
        val i = input.split("\n\n").first()
            .splitLines()
            .map { it.toList() }

        val faceA = i.slice(0..49).map { it.slice(50..99) }.toMatrix().let { Cube.FaceA(it) }
        val faceB = i.slice(0..49).map { it.slice(100..149) }.toMatrix().let { Cube.FaceB(it) }
        val faceC = i.slice(50..99).map { it.slice(50..99) }.toMatrix().let { Cube.FaceC(it) }
        val faceD = i.slice(100..149).map { it.slice(0..49) }.toMatrix().let { Cube.FaceD(it) }
        val faceE = i.slice(100..149).map { it.slice(50..99) }.toMatrix().let { Cube.FaceE(it) }
        val faceF = i.slice(150..199).map { it.slice(0..49) }.toMatrix().let { Cube.FaceF(it) }

        val cube = Cube(
            MatrixPoint(0, 0),
            faceA,
            East,
            faceA,
            faceB,
            faceC,
            faceD,
            faceE,
            faceF,
        )

//        val instructions = "10R10L10R10L10R10".parseInstructions()


        instructions.forEach { instruction ->
            cube follow instruction
//            instruction.print()
//            cube.print()
        }
        cube.print()

        return 1000 * (cube.position.row + 1 + 100) + (cube.position.col + 1 + 0) * 4 + 1
    }

    private fun String.parseInstructions() = this.split("\n\n").last()
        .let { instructions ->

            val numbers = instructions.split("""R|L""".toRegex()).map { it.toInt() }
            val rotations = instructions.split("""\d+""".toRegex()).filter(String::isNotBlank).map(Rotation::of)

            buildList {
                add(Forward(numbers.first()))
                numbers.drop(1).forEachIndexed { index, number ->
                    add(Turn(rotations[index]))
                    add(Forward(number))
                }
            }
        }

    data class Santa(val position: Position, val direction: Direction4)

    private infix fun Santa.follow(instruction: Instruction): Santa {
        return when (instruction) {
            is Forward -> go(instruction.amount)
            is Turn -> turn(instruction.rotation)
        }
    }

    private fun Santa.go(amount: Int): Santa {
        return (1..amount)
            .asSequence()
            .runningFold(this) { santa, _ ->
                val position = map.move(santa.position, direction)
                Santa(position, direction)

            }
            .takeWhile { santa -> map.isOpenTile(santa.position) }
            .last()
    }

    private fun Santa.turn(rotation: Rotation) = Santa(position, direction.rotateBy(rotation))

    private fun calculateFinalPassword(santa: Santa): Int {
        val (col, row) = santa.position
        val facing = (santa.direction.ordinal + 3) % Direction4.values().size
        return 1000 * (row + 1) + 4 * (col + 1) + facing
    }

    sealed class Instruction()
    data class Forward(val amount: Int) : Instruction()
    data class Turn(val rotation: Rotation) : Instruction()

    class Map(input: String) {
        val map = input.split("\n\n").first()
            .splitLines()
            .map { it.toList() }
        val positions = map.flatMapIndexed { row, rows ->
            rows.mapIndexed { col, char ->
                if (char.toString().isBlank()) null
                else Position(col, row)
            }.filterNotNull()
        }.toSet()

        fun move(position: Position, direction: Direction4): Position {
            val target = position.doMovement(direction, false)
            return if (target !in positions) {
                when (direction) {
                    North -> Position(target.x, positions.filter { it.x == target.x }.maxOf { it.y })
                    East -> Position(positions.filter { it.y == target.y }.minOf { it.x }, target.y)
                    South -> Position(target.x, positions.filter { it.x == target.x }.minOf { it.y })
                    West -> Position(positions.filter { it.y == target.y }.maxOf { it.x }, target.y)
                }
            } else target
        }

        fun isOpenTile(position: Position) = map[position.y][position.x] == '.'
    }

    class Cube(
        initialPosition: MatrixPoint,
        initialFace: Face,
        initialDirection: Direction4,
        val faceA: FaceA,
        val faceB: FaceB,
        val faceC: FaceC,
        val faceD: FaceD,
        val faceE: FaceE,
        val faceF: FaceF,
    ) {
        var position = initialPosition
        var face = initialFace
        var direction = initialDirection
        var moves = 0

        sealed class Face(val matrix: Matrix<Char>) {
            infix fun contains(position: MatrixPoint): Boolean {
                val (row, col) = position
                return row in matrix.rowIndices && col in matrix.colIndices
            }

            fun flipColOf(point: MatrixPoint) = MatrixPoint(0, (matrix.numberOfCols - 1) - point.col)

            fun flipRowOf(point: MatrixPoint) = MatrixPoint((matrix.numberOfRows - 1) - point.row, 0)

            fun maxRowOf(point: MatrixPoint) = MatrixPoint(matrix.numberOfRows - 1, point.col)

            fun maxColOf(point: MatrixPoint) = MatrixPoint(point.row, matrix.numberOfCols - 1)
            fun minRowOf(point: MatrixPoint) = MatrixPoint(0, point.col)
            fun minColOf(point: MatrixPoint) = MatrixPoint(point.row, 0)

            infix fun hasAnObjectOn(position: MatrixPoint) = matrix[position] == '#'

            override fun toString(): String {
                return this.javaClass.simpleName
            }

        }

        class FaceA(matrix: Matrix<Char>) : Face(matrix)
        class FaceB(matrix: Matrix<Char>) : Face(matrix)
        class FaceC(matrix: Matrix<Char>) : Face(matrix)
        class FaceD(matrix: Matrix<Char>) : Face(matrix)
        class FaceE(matrix: Matrix<Char>) : Face(matrix)
        class FaceF(matrix: Matrix<Char>) : Face(matrix)

        fun move() {
            moves++
            val memo = Triple(position, face, direction)
            val target = position.moveTo(direction)
            if (face contains target) {
                position = target
            } else {
                when (face) {
                    is FaceA -> when (direction) {
                        North -> {
                            face = faceF
                            direction = East
                            position = position.flip()
                        }

                        East -> {
                            face = faceB
                            position = MatrixPoint(position.row, 0)
                        }

                        South -> {
                            face = faceC
                            position = MatrixPoint(0, position.col)
                        }

                        West -> {
                            face = faceD
                            direction = East
                            position = face.flipRowOf(position)
                        }
                    }

                    is FaceB -> when (direction) {
                        North -> {
                            face = faceF
                            position = face.maxRowOf(position)
                        }

                        East -> {
                            face = faceE
                            direction = West
                            position = face.flipRowOf(position)
                        }

                        South -> {
                            face = faceC
                            direction = West
                            position = position.flip()
                        }

                        West -> {
                            face = faceA
                            position = face.maxColOf(position)
                        }
                    }

                    is FaceC -> when (direction) {
                        North -> {
                            face = faceA
                            position = face.maxRowOf(position)
                        }

                        East -> {
                            face = faceB
                            direction = North
                            position = position.flip()
                        }

                        South -> {
                            face = faceE
                            position = face.minRowOf(position)
                        }

                        West -> {
                            face = faceD
                            direction = South
                            position = position.flip()
                        }
                    }

                    is FaceD -> when (direction) {
                        North -> {
                            face = faceC
                            direction = East
                            position = position.flip()
                        }

                        East -> {
                            face = faceE
                            position = face.minColOf(position)
                        }

                        South -> {
                            face = faceF
                            position = face.minRowOf(position)
                        }

                        West -> {
                            face = faceA
                            direction = East
                            position = face.flipRowOf(position)
                        }
                    }

                    is FaceE -> when (direction) {
                        North -> {
                            face = faceC
                            position = face.maxRowOf(position)
                        }

                        East -> {
                            face = faceB
                            direction = West
                            position = face.flipRowOf(position)
                        }

                        South -> {
                            face = faceF
                            direction = West
                            position = position.flip()
                        }

                        West -> {
                            face = faceD
                            position = face.maxColOf(position)
                        }
                    }

                    is FaceF -> when (direction) {
                        North -> {
                            face = faceD
                            position = face.maxRowOf(position)
                        }

                        East -> {
                            face = faceE
                            direction = North
                            position = position.flip()
                        }

                        South -> {
                            face = faceB
                            position = face.minRowOf(position)
                        }

                        West -> {
                            face = faceA
                            direction = South
                            position = position.flip()
                        }
                    }
                }
            }
            if (face hasAnObjectOn position) {
                position = memo.first
                face = memo.second
                direction = memo.third
            }
        }

        private fun MatrixPoint.flip() = MatrixPoint(this.col, this.row)
        infix fun follow(instruction: Instruction) {
            when (instruction) {
                is Forward -> {
                    repeat(instruction.amount) { move() }
                }

                is Turn -> direction = direction.rotateBy(instruction.rotation)
            }
        }

        override fun toString(): String {
            return "Cube(position: $position, face: $face, direction: $direction)"
        }

    }
}           