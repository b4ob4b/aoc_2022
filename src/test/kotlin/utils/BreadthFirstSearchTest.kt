package utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BreadthFirstSearchTest {

    private val maze = """
        ###################
        #S       #        #
        # ###### ### #### #
        # #  E   #      # #
        # ###### ######## #
        #      #          #
        ###################
    """.trimIndent().toGrid(filterBlanks = false).toMatrix()

    @Test
    fun findShortestPathBetween() {
        val start = maze.search("S").first()
        val end = maze.search("E").first()

        val bfs = BreadthFirstSearch(maze)
        bfs.findShortestPathBetween(start, end) { _, neighbour -> maze[neighbour] == " " } shouldBe listOf(
            Position(1, 2),
            Position(1, 3),
            Position(1, 4),
            Position(1, 5),
            Position(1, 6),
            Position(1, 7),
            Position(1, 8),
            Position(1, 9),
            Position(2, 9),
            Position(3, 9),
            Position(3, 8),
            Position(3, 7),
            Position(3, 6),
        )
    }

    @Test
    fun printPath() {
        val maze = """
                ###################
                #        #        #
                # ###### ### #### #
                # #      #     S# #
                # ###### ######## #
                #     E#          #
                ###################
            """.trimIndent().toGrid(filterBlanks = false).toMatrix()
        val start = maze.search("S").first()
        val end = maze.search("E").first()

        val bfs = BreadthFirstSearch(maze)
        val p = bfs.findShortestPathBetween(start, end) { _, neighbour -> maze[neighbour] == " " }
        maze.insertAt(p.drop(1).dropLast(1).associateWith { "." }).print()

    }

}