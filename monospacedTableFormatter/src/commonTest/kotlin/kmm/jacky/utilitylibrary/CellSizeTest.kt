package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.CellSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CellSizeTest {

    @Test
    fun validateCellSizePercentage() {
        assertEquals(0.0, CellSize.Percentage(0.0).factor)
        assertEquals(0.1, CellSize.Percentage(0.1).factor)
        assertEquals(0.5, CellSize.Percentage(0.5).factor)
        assertEquals(0.9, CellSize.Percentage(0.9).factor)
        assertEquals(1.0, CellSize.Percentage(1.0).factor)

        expectException<IllegalArgumentException> { CellSize.Percentage(-0.1) }

        expectException<IllegalArgumentException> { CellSize.Percentage(1.1) }
    }

    private inline fun <reified T> expectException(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            assertTrue(e is T)
        }
    }
}