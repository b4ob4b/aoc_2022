import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day07(IO.TYPE.SAMPLE).test(95437, 24933642)
    Day07().solve()
}

class Day07(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("No Space Left On Device", inputType = inputType) {

    val origin = Dir(name = "/")

    data class File(val name: String, val size: Int)
    data class Dir(
        val name: String,
        val dirs: MutableList<Dir> = mutableListOf(),
        val files: MutableList<File> = mutableListOf(),
        val parent: Dir? = null,
        var size: Int = 0
    ) {
        fun add(dir: Dir) {
            dirs.add(dir)
        }

        fun add(file: File) {
            files.add(file)
        }

        fun changeDirectory(name: String): Dir? {
            return dirs.singleOrNull { it.name == name }
        }

        fun calculateSize() {
            dirs.forEach { it.calculateSize() }
            size += files.map { it.size }.sum() + dirs.sumOf { it.size }
        }

        fun findDirsWithLessSizeThan(maxSize: Int): List<Int> {
            val sizes = mutableListOf<Int>()
            if (this.size <= maxSize) sizes.add(this.size)
            sizes.addAll(0, dirs.flatMap { it.findDirsWithLessSizeThan(maxSize) })
            return sizes
        }

        override fun toString(): String {
            return "Dir(name='$name', dirs=$dirs, files=$files, size=$size)"
        }
    }


    override fun part1(): Int {

        var current = origin
        input.split("$ ").drop(2)
            .map {
                if (it.startsWith("ls")) {
                    val lines = it.splitLines().filter { it.isNotBlank() }.drop(1)
                    lines.forEach {
                        if (it.startsWith("dir")) {
                            current.add(Dir(it.drop(4), parent = current))
                        } else {
                            val info = it.split(" ")
                            current.add(File(info[1], info[0].toInt()))
                        }
                    }
                } else {
                    val newDir = it.split(" ")[1].removeSuffix("\n")
                    if (newDir != "..") {
                        current = current.changeDirectory(newDir)!!
                    } else {
                        current = current.parent!!
                    }
                }
            }
        origin.calculateSize()
        return origin.findDirsWithLessSizeThan(100000).sum()
    }

    override fun part2(): Int {
        val diskspace = 70000000
        val spaceUpdate = 30000000
        val spaceToFree = (spaceUpdate - (diskspace - origin.size))
        return origin.findDirsWithLessSizeThan(spaceUpdate).filter { it > spaceToFree }.minOf { it }
    }
}           