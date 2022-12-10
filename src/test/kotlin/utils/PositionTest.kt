package utils

import io.kotest.matchers.collections.shouldNotContainInOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

internal class PositionTest {

    @Test
    fun manhattenDistance() {
        Position(1, 2).manhattenDistance shouldBe 3
        Position(1, -2).manhattenDistance shouldBe 3
        Position(-1, -2).manhattenDistance shouldBe 3
        Position(-1, 2).manhattenDistance shouldBe 3
    }

    @Test
    fun doMovement() {
        Position(0, 0).doMovement(Direction4.East) shouldBe Position(1, 0)
        Position(0, 0).doMovement(Direction4.North) shouldBe Position(0, 1)
        Position(0, 0).doMovement(Direction4.West) shouldBe Position(-1, 0)
        Position(0, 0).doMovement(Direction4.South) shouldBe Position(0, -1)
    }

    @Test
    fun get4Neighbours() {
        Position(0, 0).get4Neighbours().toList() shouldNotContainInOrder listOf(
            Position(1, 0),
            Position(0, 1),
            Position(-1, 0),
            Position(0, -1),
        )
    }

    @Test
    fun get8Neighbours() {
        Position(0, 0).get4Neighbours().toList() shouldNotContainInOrder listOf(
            Position(0, 1),
            Position(1, 1),
            Position(1, 0),
            Position(1, -1),
            Position(0, -1),
            Position(-1, -1),
            Position(-1, 0),
            Position(-1, 1),
        )
    }
}