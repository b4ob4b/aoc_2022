import utils.*
import kotlin.math.abs

fun main() {
    Day15(IO.TYPE.SAMPLE).test(26, 56000011L)
    Day15().solve()
}

class Day15(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("", inputType = inputType) {

    data class Signal(val sensor: Position, val beacon: Position) {
        val manhattenDistance
            get() = (sensor - beacon).manhattenDistance
    }

    private val distressRow = if (isTest) 10 else 2_000_000
    private val coordinateRange = if (isTest) 0..20 else 0..4_000_000

    private val signals = input.splitLines()
        .map { it.split("""at |:""".toRegex()) }
        .map { (_, sensor, _, beacon) -> Signal(sensor.toPosition(), beacon.toPosition()) }

    override fun part1(): Any? {
        val affectedSignals = signals
            .filter {
                val y = it.sensor.y
                val distance = abs(y - distressRow)
                distance <= it.manhattenDistance
            }

        val distressPositions = affectedSignals.fold(emptySet<Position>()) { distessLine, signal ->
            distessLine + listOf(Position.left, Position.right).flatMap { direction ->
                Position(signal.sensor.x, distressRow)
                    .getPathThrough(direction)
                    .takeWhile { (it - signal.sensor).manhattenDistance <= signal.manhattenDistance }
                    .toSet()
            }
        }

        return (distressPositions - signals.map { it.beacon }.toSet()).size
    }

    override fun part2(): Long {
        val possibleMissingBeaconPositions = signals
            .flatMap { it.sensor.boundaryOf(it.manhattenDistance + 1) }
            .filter { (x, y) -> x in coordinateRange && y in coordinateRange }
            .toSet()
        return possibleMissingBeaconPositions.first { possibleMissingBeacon ->
            val inRange = signals.any { signal ->
                val distancePointToSensor = (signal.sensor - possibleMissingBeacon).manhattenDistance
                val signalStrength = signal.manhattenDistance + 1
                val isPointInRange = distancePointToSensor < signalStrength
                isPointInRange
            }
            !inRange
        }.calculateTuningFrequency()
    }

    private fun Position.calculateTuningFrequency() = this.x * 4000000L + this.y

    private fun Position.boundaryOf(distance: Int): Set<Position> {
        val range = (0..distance)
        val negativeRange = range.map { it * -1 }
        val rangeReversed = range.reversed()
        val negativeReversed = negativeRange.reversed()


        val q1 = range.zip(rangeReversed)
        val q2 = negativeRange.zip(rangeReversed)
        val q3 = negativeRange.zip(negativeReversed)
        val q4 = range.zip(negativeReversed)

        return listOf(q1, q2, q3, q4).flatten().map { Position(it.first, it.second) + this }.toSet()
    }
}