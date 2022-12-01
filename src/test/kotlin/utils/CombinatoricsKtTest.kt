package utils

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CombinatoricsKtTest {
    @Test
    fun `find 0 combinations`() {
        listOf(1,2,3).combinations(0) shouldBe emptySequence<Int>()
    }

    @Test
    fun `find 1 combination`() {
        listOf(1,2,3).combinations(1).toList() shouldBe listOf(listOf(1),listOf(2),listOf(3))
    }

    @Test
    fun `find all tuple combinations`() {
        listOf(1,2,3).combinations(2).toList() shouldBe listOf(
            listOf(1,2),
            listOf(1,3),
            listOf(2,3)
        )

        listOf(1,2,3,4).combinations(2).toList() shouldBe listOf(
            listOf(1,2),
            listOf(1,3),
            listOf(1,4),
            listOf(2,3),
            listOf(2,4),
            listOf(3,4),
        )
    }
}
