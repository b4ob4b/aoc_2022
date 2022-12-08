import utils.Day
import utils.IO
import utils.splitLines

fun main() {
    Day07(IO.TYPE.SAMPLE).test(95437, 24933642)
    Day07().solve()
}

class Day07(inputType: IO.TYPE = IO.TYPE.INPUT) : Day("No Space Left On Device", inputType = inputType) {

    private val origin = File("/", 0, parent = null)
    private val terminalOutput = input.split("$ ").drop(2)
    
    enum class Type { File, Directory }

    data class File(
        val name: String,
        var size: Int = 0,
        val children: MutableList<File> = mutableListOf(),
        val parent: File?,
        val type: Type = Type.Directory
    ) {

        fun add(file: File) = children.add(file)

        fun add(files: List<File>) = files.forEach(children::add)

        fun changeDirectory(name: String): File? {
            return children.singleOrNull { it.name == name }
        }

        fun calculateSize() {
            children.forEach { it.calculateSize() }
            size += children.map { it.size }.sum()
        }

        fun findDirsWithLessSizeThan(maxSize: Int): List<Int> {
            val sizes = mutableListOf<Int>()
            if (this.type == Type.Directory && this.size <= maxSize) sizes.add(this.size)
            sizes.addAll(0, children.flatMap { it.findDirsWithLessSizeThan(maxSize) })
            return sizes
        }
    }


    private fun String.changeDirectory(currentDirectory: File): File {
        val newDirectory = this.split(" ").last()
        return if (newDirectory == "..") currentDirectory.parent!! else currentDirectory.changeDirectory(newDirectory)!!
    }

    private fun List<String>.createFiles(currentDirectory: File) {
        this.forEach {
            if (it.startsWith("dir")) {
                currentDirectory.add(File(it.split(" ").last(), parent = currentDirectory))
            } else {
                val (size, name) = it.split(" ")
                currentDirectory.add(File(name, size = size.toInt(), parent = currentDirectory, type = Type.File))
            }
        }
    }

    override fun part1(): Int {
        var currentDirectory = origin.copy()
        terminalOutput.map { it.splitLines().filter { it.isNotBlank() } }.forEach {
            when (it.first().slice(0..1)) {
                "ls" -> it.drop(1).createFiles(currentDirectory)
                "cd" -> currentDirectory = it.single().changeDirectory(currentDirectory)
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