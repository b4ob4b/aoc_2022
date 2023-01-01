import utils.Day
import utils.IO
import utils.matrix.Matrix
import utils.matrix.Position
import utils.navigation.Direction4.*
import utils.navigation.Direction4
import utils.navigation.Rotation
import utils.splitLines
import utils.toMatrix

fun main() {
    Day22(IO.TYPE.SAMPLE).test(6032)
    Day22().solve()
}

class Day22(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Monkey Map", inputType = inputType) {

    private val instructions = input.parseInstructions()

    override fun part1(): Int {
        val field = input.parseField()
        val initialPosition = Position(0, field.first().indexOfFirst { it == '.' })

        val flatField = FlatField(field, initialPosition, East)

        instructions.forEach { instruction -> flatField follow instruction }

        return calculateFinalPassword(flatField.position.row, flatField.position.col, flatField.direction)
    }

    override fun part2(): Int {
        val field = input.parseField()

        val facesCoordinates = listOf(
            Position(0, 50) to Position(49, 99),
            Position(0, 100) to Position(49, 149),
            Position(50, 50) to Position(99, 99),
            Position(100, 0) to Position(149, 49),
            Position(100, 50) to Position(149, 99),
            Position(150, 0) to Position(199, 49),
        )
        val faces = listOf(
            FaceA(Face.of(field, facesCoordinates[0])),
            FaceB(Face.of(field, facesCoordinates[1])),
            FaceC(Face.of(field, facesCoordinates[2])),
            FaceD(Face.of(field, facesCoordinates[3])),
            FaceE(Face.of(field, facesCoordinates[4])),
            FaceF(Face.of(field, facesCoordinates[5])),
        )

        val cube = Cube(
            faces,
            Position(0, 0),
            faces.single { it is FaceA },
            East,
        )

        instructions.forEach { instruction -> cube follow instruction }

        val offset = cube.face.getPositionOffset(faces, facesCoordinates)

        val row = cube.position.row + offset.row
        val col = cube.position.col + offset.col
        val direction = cube.direction

        return calculateFinalPassword(row, col, direction)
    }

    private fun Face.getPositionOffset(faces: List<Face>, facesCoordinates: List<Pair<Position, Position>>): Position {
        return facesCoordinates[faces.indexOfFirst { it.javaClass == this.javaClass }].first
    }

    private fun String.parseField() = this
        .split("\n\n")
        .first()
        .splitLines()
        .map { it.toList() }

    private fun String.parseInstructions() = this.split("\n\n").last()
        .let { instructions ->

            val numbers = instructions.split("""[RL]""".toRegex()).map { it.toInt() }
            val rotations = instructions.split("""\d+""".toRegex()).filter(String::isNotBlank).map(Rotation::of)

            buildList {
                add(Forward(numbers.first()))
                numbers.drop(1).forEachIndexed { index, number ->
                    add(Turn(rotations[index]))
                    add(Forward(number))
                }
            }
        }

    private fun calculateFinalPassword(row: Int, column: Int, direction: Direction4): Int {
        val facing = (direction.ordinal + 3) % Direction4.values().size
        return 1000 * (row + 1) + 4 * (column + 1) + facing
    }

    sealed class Instruction
    data class Forward(val amount: Int) : Instruction()
    data class Turn(val rotation: Rotation) : Instruction()


    abstract class Field(initialDirection: Direction4) {

        var direction = initialDirection

        abstract fun move()
        infix fun follow(instruction: Instruction) {
            when (instruction) {
                is Forward -> {
                    repeat(instruction.amount) { move() }
                }

                is Turn -> direction = direction.rotateBy(instruction.rotation)
            }
        }
    }

    class FlatField(
        private val field: List<List<Char>>,
        initialPosition: Position,
        initialDirection: Direction4,
    ) : Field(initialDirection) {

        var position = initialPosition
        private val positions = field.flatMapIndexed { row, rows ->
            rows.mapIndexed { col, char ->
                if (char.toString().isBlank()) null
                else Position(row, col)
            }.filterNotNull()
        }.toSet()

        override fun move() {
            val memo = position
            val target = position.moveTo(direction)
            position = if (target !in positions) {
                when (direction) {
                    North -> Position(positions.filter { it.col == target.col }.maxOf { it.row }, target.col)
                    East -> Position(target.row, positions.filter { it.row == target.row }.minOf { it.col })
                    South -> Position(positions.filter { it.col == target.col }.minOf { it.row }, target.col)
                    West -> Position(target.row, positions.filter { it.row == target.row }.maxOf { it.col })
                }
            } else {
                target
            }

            if (hasObject()) {
                position = memo
            }
        }

        private fun hasObject() = field[position.row][position.col] == '#'
    }

    /*
    * Cube
    *   expects faces in following order
    *        AB
    *        C
    *       DE
    *       F
    * */
    class Cube(
        faces: List<Face>,
        initialPosition: Position,
        initialFace: Face,
        initialDirection: Direction4,
    ) : Field(initialDirection) {

        private val faceA = faces[0]
        private val faceB = faces[1]
        private val faceC = faces[2]
        private val faceD = faces[3]
        private val faceE = faces[4]
        private val faceF = faces[5]

        var position = initialPosition
        var face = initialFace

        override fun move() {
            val memo = Triple(position, face, direction)
            val target = position.moveTo(direction)
            if (face contains target) {
                position = target
            } else {
                face.flip()
            }
            if (face hasAnObjectOn position) {
                position = memo.first
                face = memo.second
                direction = memo.third
            }
        }

        private fun Face.flip() = when (this) {
            is FaceA -> this.flip()
            is FaceB -> this.flip()
            is FaceC -> this.flip()
            is FaceD -> this.flip()
            is FaceE -> this.flip()
            is FaceF -> this.flip()
        }

        private fun FaceA.flip() = when (direction) {
            North -> {
                face = faceF
                direction = East
                position = face.flip(position)
            }

            East -> {
                face = faceB
                position = Position(position.row, 0)
            }

            South -> {
                face = faceC
                position = Position(0, position.col)
            }

            West -> {
                face = faceD
                direction = East
                position = face.flipRowOf(position)
            }
        }

        private fun FaceB.flip() = when (direction) {
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
                position = face.flip(position)
            }

            West -> {
                face = faceA
                position = face.maxColOf(position)
            }
        }

        private fun FaceC.flip() = when (direction) {
            North -> {
                face = faceA
                position = face.maxRowOf(position)
            }

            East -> {
                face = faceB
                direction = North
                position = face.flip(position)
            }

            South -> {
                face = faceE
                position = face.minRowOf(position)
            }

            West -> {
                face = faceD
                direction = South
                position = face.flip(position)
            }
        }

        private fun FaceD.flip() = when (direction) {
            North -> {
                face = faceC
                direction = East
                position = face.flip(position)
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

        private fun FaceE.flip() = when (direction) {
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
                position = face.flip(position)
            }

            West -> {
                face = faceD
                position = face.maxColOf(position)
            }
        }

        private fun FaceF.flip() = when (direction) {
            North -> {
                face = faceD
                position = face.maxRowOf(position)
            }

            East -> {
                face = faceE
                direction = North
                position = face.flip(position)
            }

            South -> {
                face = faceB
                position = face.minRowOf(position)
            }

            West -> {
                face = faceA
                direction = South
                position = face.flip(position)
            }
        }
    }

    sealed class Face(private val field: Matrix<Char>) {
        infix fun contains(position: Position): Boolean {
            val (row, col) = position
            return row in field.rowIndices && col in field.colIndices
        }

        fun flipRowOf(point: Position) = Position((field.numberOfRows - 1) - point.row, 0)

        fun maxRowOf(point: Position) = Position(field.numberOfRows - 1, point.col)

        fun maxColOf(point: Position) = Position(point.row, field.numberOfCols - 1)
        fun minRowOf(point: Position) = Position(0, point.col)
        fun minColOf(point: Position) = Position(point.row, 0)
        fun flip(position: Position) = Position(position.col, position.row)

        infix fun hasAnObjectOn(position: Position) = field[position] == '#'

        companion object {
            fun of(field: List<List<Char>>, between: Pair<Position, Position>): Matrix<Char> {
                return field
                    .slice(between.first.row..between.second.row)
                    .map { it.slice(between.first.col..between.second.col) }
                    .toMatrix()
            }
        }

    }

    class FaceA(face: Matrix<Char>) : Face(face)
    class FaceB(face: Matrix<Char>) : Face(face)
    class FaceC(face: Matrix<Char>) : Face(face)
    class FaceD(face: Matrix<Char>) : Face(face)
    class FaceE(face: Matrix<Char>) : Face(face)
    class FaceF(face: Matrix<Char>) : Face(face)
}