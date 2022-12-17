import utils.*

fun main() {
    Day17(IO.TYPE.SAMPLE).test(3068)
    Day17().solve()
}

class Day17(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Pyroclastic Flow", inputType = inputType) {

    override fun part1(): Int {
        val tetris = Tetris(input)
        tetris.playRounds(2022)
        return tetris.getTowerHeight()
    }

    override fun part2(): Long {
        val rounds = 1_000_000_000_000L
        val offsetRounds = 1729
        val repeatingRounds = 1735
        val repeatedHeight = 2720

        val repeatingHeights = (rounds - offsetRounds) / repeatingRounds
        val remainingRounds = (rounds - offsetRounds) % repeatingHeights

        val tetris = Tetris(input)
        tetris.playRounds(remainingRounds.toInt() + offsetRounds)

        return repeatingHeights * repeatedHeight + tetris.getTowerHeight()
    }
    
    class Tetris(gasJet: String) {

        private var gasJetStream = sequence {
            var i = 0
            while (true) {
                yield(gasJet[i % gasJet.length])
                i++
            }
        }.iterator()

        private val bottom = (1..7).map { Position(it, 0) }.toSet()
        private val walls = Pair(0, 8)
        private val rocks = mutableSetOf<Position>()

        private val shapes = listOf(
            { Minus() },
            { Cross() },
            { L() },
            { I() },
            { Block() },
        )

        private fun Shape.move(direction: Char) = when (direction) {
            '<' -> if (this.touchesLeft()) Position.origin else Position.left
            '>' -> if (this.touchesRight()) Position.origin else Position.right
            else -> Position.origin
        }

        private fun Shape.touchesRockOrBottom(): Boolean {
            val nextPosition = this.getPositions().map { it + Position.down }.toSet()
            return nextPosition.intersect((rocks + bottom)).isNotEmpty()
        }

        private fun Shape.touchesLeft(): Boolean {
            val touchesWall = this.getMostLeft() - 1 == walls.first
            val touchesRock = (this.getPositions().map { it + Position.left }.toSet() intersect rocks).isNotEmpty()
            return touchesWall || touchesRock
        }

        private fun Shape.touchesRight(): Boolean {
            val touchesWall = this.getMostRight() + 1 == walls.second
            val touchesRock = (this.getPositions().map { it + Position.right }.toSet() intersect rocks).isNotEmpty()
            return touchesWall || touchesRock
        }

        fun getTowerHeight() = (rocks.maxOfOrNull { it.y } ?: 0)

        fun playRounds(rounds: Int) {
            
            for (stepNumber in (0 until rounds)) {
                
                val currentShape = shapes[stepNumber % shapes.size].invoke()
                val yOffset = getTowerHeight() - currentShape.getLowestYPosition() + 3
                currentShape.offset += Position(0, yOffset)
                
                while (true) {
                    when (currentShape.state) {
                        State.Falling -> {
                            currentShape.offset += Position.down
                            currentShape.state = State.Sliding
                        }
                        State.Sliding -> {
                            currentShape.offset += currentShape.move(gasJetStream.next())
                            if (currentShape.touchesRockOrBottom()) {
                                currentShape.state = State.Resting
                            } else {
                                currentShape.state = State.Falling
                            }
                        }
                        State.Resting -> {
                            rocks.addAll(currentShape.getPositions())
                            break
                        }
                    }
                }
            }
        }
    }

    private sealed class Shape(
        private val positions: Set<Position>,
        var offset: Position,
        var state: State = State.Sliding,
    ) {
        fun getLowestYPosition() = positions.minOfOrNull { it.y } ?: 0
        fun getMostLeft() = getPositions().minOf { it.x }
        fun getMostRight() = getPositions().maxOf { it.x }
        fun getPositions() = positions.map { it + offset }.toSet()
    }

    private class Minus : Shape(
        setOf(
            Position(-1, 0), Position(0, 0), Position(1, 0), Position(2, 0)
        ), Position(4, 1)
    )

    private class Cross : Shape(
        setOf(
            Position(0, 1),
            Position(-1, 0), Position(0, 0), Position(1, 0),
            Position(0, -1),
        ), Position(4, 1)
    )

    private class L : Shape(
        setOf(
            Position(1, 1),
            Position(1, 0),
            Position(-1, -1), Position(0, -1), Position(1, -1),
        ), Position(4, 1)
    )

    private class I : Shape(
        setOf(
            Position(-1, 2),
            Position(-1, 1),
            Position(-1, 0),
            Position(-1, -1),
        ), Position(4, 1)
    )

    private class Block : Shape(
        setOf(
            Position(-1, 1), Position(0, 1),
            Position(-1, 0), Position(0, 0),
        ), Position(4, 1)
    )

    enum class State { Falling, Sliding, Resting,  }
}           