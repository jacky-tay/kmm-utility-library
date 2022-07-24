package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.extensions.isValid
import kmm.jacky.utilitylibrary.models.column.Column
import kotlin.test.Test
import kotlin.test.assertEquals

class ColumnDefinitionValidationTest {

    @Test
    fun validateValidColumnDefinitions() {
        verify(40, true, CellSize.EquallySpacing())

        verify(
            40, true,
            CellSize.Percentage(0.1), CellSize.Percentage(0.2), CellSize.Percentage(0.7)
        )

        verify(
            40, true,
            CellSize.FixedWidth(10), CellSize.FixedWidth(20), CellSize.FixedWidth(4)
        )
    }

    @Test
    fun validateInvalidColumnDefinitions() {
        verify(
            40, false,
            CellSize.Percentage(0.2), CellSize.Percentage(0.2), CellSize.Percentage(0.7)
        )

        verify(
            40, false,
            CellSize.FixedWidth(10), CellSize.FixedWidth(20), CellSize.FixedWidth(10)
        )
    }

    @Test
    fun validateComplexColumnDefinitions() {
        verify(
            40,
            true,
            CellSize.FixedWidth(10),
            CellSize.Percentage(0.5),
            CellSize.EquallySpacing(),
            CellSize.ShrinkToFit,
            CellSize.ExpandToFit
        )
    }

    private fun verify(width: Int, expectation: Boolean, vararg size: CellSize) {
        val result = size.map { Column.Definition(size = it) }.isValid(width)
        assertEquals(expectation, result)
    }
}
