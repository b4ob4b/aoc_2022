import utils.*

fun main() {
    Day14(IO.TYPE.SAMPLE).test(24, 93)
    Day14().solve()
}

class Day14(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val states = SandReservoir(input)
        .dropsSand()
        .takeWhile { it != SandReservoir.State.Full }
        .toList()
    
    override fun part1(): Int {
        return states.indexOfFirst { it == SandReservoir.State.Lost } 
    }

    override fun part2(): Int {
        return states.count { it == SandReservoir.State.Resting }
    }

    private class SandReservoir(wallInformation: String) {
        private val rocks = wallInformation.splitLines().flatMap {
                val corners = it.split(" -> ").map { it.getPosition() }
                corners.zipWithNext().flatMap { it.getLine() }
            }.toSet()

        private val deepestRock = rocks.maxOf { it.y }
        private val source = Position(500, 0)
        private val reservoirBottom = deepestRock + 2
        private val sandPositions = mutableSetOf<Position>()
        private val sand = Sand(source, State.Falling)
        private val down = Position(0, 1)
        private val downLeft = Position(-1, 1)
        private val downRight = Position(1, 1)

        private fun reset() {
            sand.position = source
            sand.state = State.Falling
        }


        fun dropsSand() = sequence {
            while (true) {
                when (sand.state) {
                    State.Falling -> sandFalls()
                    State.Rolling -> sandRolls()
                    State.Lost -> sand.state = State.Rolling
                    else -> {}
                }
            }
        }

        private suspend fun SequenceScope<State>.sandFalls() {
            val hitPosition = sand.position.getPathThrough(down).takeWhile { it.touchesObstacle() }.last()
            if (hitPosition.y >= deepestRock) yield(State.Lost)
            sand.position = hitPosition
            sand.state = State.Rolling
        }
        
        private fun Position.touchesObstacle() = rocks.contains(this).not() && sandPositions.contains(this).not() && this.y < reservoirBottom
        
        private fun Sand.hitsObstacle(position: Position): Boolean {
            return (this.position + position) in sandPositions || (this.position + position) in rocks || (this.position + position).y == reservoirBottom
        }

        private suspend fun SequenceScope<State>.sandRolls() {
            if (sand.hitsObstacle(downLeft).not()) {
                sandLooksLeft()
            } else if (sand.hitsObstacle(downRight).not()) {
                sandLooksRight()
            } else {
                sandFindsRestingPosition()
            }
        }

        private fun sandLooksLeft() {
            sand.position = sand.position + downLeft
            sand.state = State.Falling
        }

        private fun sandLooksRight() {
            sand.position = sand.position + downRight
            sand.state = State.Falling
        }

        private suspend fun SequenceScope<State>.sandFindsRestingPosition() {
            sandPositions.add(sand.position)
            yield(State.Resting)
            if (sand.position == source) {
                yield(State.Full)
            }
            reset()
        }

        private data class Sand(var position: Position, var state: State)

        enum class State {
            Falling, Rolling, Resting, Lost, Full
        }
    }
}           