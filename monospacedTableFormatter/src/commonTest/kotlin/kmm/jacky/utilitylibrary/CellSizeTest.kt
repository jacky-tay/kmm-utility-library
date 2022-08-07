package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.enums.CellSize.ShrinkToFit
import kmm.jacky.utilitylibrary.enums.CellSize.Percentage
import kmm.jacky.utilitylibrary.enums.CellSize.EquallySpacing
import kmm.jacky.utilitylibrary.enums.CellSize.ExpandToFit
import kmm.jacky.utilitylibrary.enums.CellSize.Undefined
import kmm.jacky.utilitylibrary.enums.CellSize.FixedWidth
import kmm.jacky.utilitylibrary.extensions.getWeightIfAvailable
import kmm.jacky.utilitylibrary.extensions.update
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CellSizeTest {

    @Test
    fun validateCellSizeEquallySpacing() {
        assertEquals(1, EquallySpacing(1).weight)
        assertEquals(2, EquallySpacing(2).weight)
        assertEquals(10, EquallySpacing(10).weight)
        assertEquals(100, EquallySpacing(100).weight)

        expectException<IllegalArgumentException> { EquallySpacing(0).weight }
        expectException<IllegalArgumentException> { EquallySpacing(-1).weight }
        expectException<IllegalArgumentException> { EquallySpacing(-10).weight }
    }

    @Test
    fun validateCellSizePercentage() {
        assertEquals(0.1, Percentage(0.1).factor)
        assertEquals(0.5, Percentage(0.5).factor)
        assertEquals(0.9, Percentage(0.9).factor)
        assertEquals(1.0, Percentage(1.0).factor)

        expectException<IllegalArgumentException> { Percentage(0.0) }
        expectException<IllegalArgumentException> { Percentage(-0.1) }
        expectException<IllegalArgumentException> { Percentage(1.1) }
    }

    @Test
    fun validateCellSizeFixedWidth() {
        assertEquals(1, FixedWidth(1).width)
        assertEquals(10, FixedWidth(10).width)
        assertEquals(100, FixedWidth(100).width)

        expectException<IllegalArgumentException> { FixedWidth(0).width }
        expectException<IllegalArgumentException> { FixedWidth(-1).width }
        expectException<IllegalArgumentException> { FixedWidth(-10).width }
    }

    @Test
    fun validateUpdateMethod() {
        fun assertUpdate(from: CellSize, transformed: CellSize, expect: CellSize) {
            assertEquals(expect, from.update(transformed))
        }

        val sizes = listOf(
            ShrinkToFit, Percentage(0.1), EquallySpacing(2), ExpandToFit, FixedWidth(1)
        )
        sizes.forEach { lhs ->
            sizes.forEach { rhs ->
                assertUpdate(lhs, rhs, lhs)
                assertUpdate(rhs, lhs, rhs)
            }
            assertUpdate(lhs, Undefined, lhs)
            assertUpdate(Undefined, lhs, lhs)
        }
        assertUpdate(Undefined, Undefined, Undefined)
    }

    @Test
    fun validateGetWeightIfAvailable() {
        fun assertWeight(size: CellSize, expected: Int) {
            assertEquals(expected, size.getWeightIfAvailable())
        }

        assertWeight(EquallySpacing(1), 1)
        assertWeight(EquallySpacing(2), 2)
        assertWeight(ExpandToFit, 1)

        assertWeight(Undefined, 0)
        assertWeight(ShrinkToFit, 0)
        assertWeight(FixedWidth(1), 0)
        assertWeight(FixedWidth(10), 0)
        assertWeight(Percentage(0.1), 0)
        assertWeight(Percentage(1.0), 0)
    }

    private inline fun <reified T> expectException(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            assertTrue(e is T)
        }
    }
}
