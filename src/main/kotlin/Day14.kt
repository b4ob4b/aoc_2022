import utils.*

fun main() {
    Day14(IO.TYPE.SAMPLE).test(24, 93)
    Day14().solve()
}

class Day14(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    private val sandReservoir = SandReservoir(input)

    private class SandReservoir(wallInformation: String) {
        private val walls = wallInformation
            .splitLines()
            .flatMap {
                val corners = it
                    .split(" -> ")
                    .map { it.getPosition() }
                corners.zipWithNext()
                    .flatMap { it.getLine() }
            }
            .toSet()

        private val deepestWall = walls.maxOf { it.y }
        private val source = Position(500, 0)
        private val offset = walls.minOf { it.x }
        private val sandPositions = mutableSetOf<Position>()
        private val sand = Sand(source, State.Falling)
        private val down = Position(0, 1)
        private val downLeft = Position(-1, 1)
        private val downRight = Position(1, 1)
        val numberOfSandDroplets
            get() = sandPositions.size 

        private fun Sand.isObject(position: Position): Boolean {
            return (this.position + position) in sandPositions || (this.position + position) in walls
        }

        fun drop(): State {
            sand.position = source
            sand.state = State.Falling
            while (sand.state !in listOf(State.Resting, State.Lost)) {
                when (sand.state) {
                    State.Falling -> {
                        val fallingPath = sand.position.getPathThrough(down)
                        val hitPosition =
                            fallingPath.takeWhile { walls.contains(it).not() && sandPositions.contains(it).not() && it.y <= deepestWall }
                                .last()
                        if (hitPosition.y >= deepestWall) {
                            sand.state = State.Lost
                        } else {
                            sand.position = hitPosition
                            sand.state = State.Rolling
                        }
                    }
                    State.Rolling -> {
                        if (sand.isObject(downLeft).not()) {
                            sand.position = sand.position + downLeft
                            sand.state = State.Falling
                        } else if (sand.isObject(downRight).not()) {
                            sand.position = sand.position + downRight
                            sand.state = State.Falling
                        } else {
                            sandPositions.add(sand.position)
                            sand.state = State.Resting
                        }
                    }
                    State.Resting -> {}
                    State.Lost -> {}
                }
            }
            return sand.state
        }

        private data class Sand(var position: Position, var state: State) {}

        enum class State {
            Falling, Rolling, Resting, Lost
        }

        private fun Set<Position>.applyOffset() = this.map { it.applyOffset() }
        private fun Position.applyOffset() = this - Position(offset, 0)

        override fun toString(): String {
            return (Matrix(walls.maxOf { it.x } - offset, deepestWall + 1) { "." })
                .insertAt(walls.applyOffset().associateWith { "#" })
                .insertAt(sandPositions.applyOffset().associateWith { "o" })
                .insertAt(source.applyOffset(), "+")
                .insertAt(Position(x=534, y=157).applyOffset(), "X")
                .transpose().toString()
        }
    }


    override fun part1(): Int {
        var state: SandReservoir.State = SandReservoir.State.Falling
        while (state != SandReservoir.State.Lost) {
            state = sandReservoir.drop()
        }
        return sandReservoir.numberOfSandDroplets
    }

    override fun part2(): Any? {
        return "not yet implement"
    }
}           