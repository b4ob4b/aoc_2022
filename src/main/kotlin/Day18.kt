import utils.*

fun main() {
    Day18(IO.TYPE.SAMPLE).test(64, 58)
    Day18().solve()
}

class Day18(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("Boiling Boulders", inputType = inputType) {

    private val cubes = input.splitLines()
        .map { it.toPosition3D() }
        .map { Cube(it) }
        .toSet()

    private val areas = cubes.flatMap { it.areas }

    override fun part1() = areas
        .groupingBy { it }
        .eachCount()
        .values
        .filter { it == 1 }
        .sum()

    override fun part2(): Int {
        val minX = cubes.minOf { it.corner.x } - 1
        val minY = cubes.minOf { it.corner.y } - 1
        val minZ = cubes.minOf { it.corner.z } - 1
        val maxX = cubes.maxOf { it.corner.x } + 1
        val maxY = cubes.maxOf { it.corner.y } + 1
        val maxZ = cubes.maxOf { it.corner.z } + 1
        
        val queue = mutableSetOf<Cube>()
        queue.add(Cube(Position3D(minX, minY, minZ)))
        
        val outside = mutableSetOf<Cube>()
        while (queue.isNotEmpty()) {
            val cube = queue.first()
            queue.remove(cube)
            outside.add(cube)
            Position3D.allDirections.map { cube + it }
                .filter {
                    it !in cubes &&
                            it !in outside &&
                            it.corner.x in minX .. maxX &&
                            it.corner.y in minY .. maxY &&
                            it.corner.z in minZ .. maxZ
                }.forEach {
                    queue.add(it)
                }
        }
        val outsideAreas = outside.flatMap { it.areas }.toSet()
        return outsideAreas.intersect(areas.toSet()).size
    }
}           