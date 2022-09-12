package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.extensions.buildColumnReferencesFromDefinitions
import kmm.jacky.utilitylibrary.extensions.currency
import kmm.jacky.utilitylibrary.extensions.end
import kmm.jacky.utilitylibrary.extensions.span
import kmm.jacky.utilitylibrary.extensions.updatePosition
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kotlin.test.Test
import kotlin.test.assertEquals

class ColumnReferenceTest {

    lateinit var references: List<Column.Reference>

    @Test
    fun testUpdatePosition() {
        references = (0 until 4).map { Column.Reference() }
        references.forEach { it.len = 2 }
        references.updatePosition()

        assertEquals(4, references.size)

        verify(0, 0, 2, 2)
        verify(1, 5, 7, 2)
        verify(2, 10, 12, 2)
        verify(3, 15, 17, 2)
    }

    @Test
    fun buildColumnReferenceTest() {
        val definitions = listOf(
            Column.Definition(size = CellSize.ExpandToFit),
            Column.Definition(size = CellSize.FixedWidth(1)),
            Column.Definition(size = CellSize.Percentage(0.5)),
            Column.Definition(size = CellSize.ShrinkToFit)
        )
        val rows = listOf(
            BaseRow(definitions, "", "", "", "1"),
            BaseRow(definitions, "", "", "", "12"),
            BaseRow(definitions, "", "", "", "12")
        )
        references = rows.buildColumnReferencesFromDefinitions(definitions, 40)
        assertEquals(4, references.size)

        verify(0, 0, 13, 13)
        verify(1, 16, 17, 1)
        verify(2, 20, 35, 15)
        verify(3, 38, 40, 2)
    }

    @Test
    fun buildColumnReferenceWithSpannedTest() {
        val definitions = listOf(
            Column.Definition(size = CellSize.EquallySpacing(2)),
            Column.Definition(size = CellSize.EquallySpacing(1)),
            Column.Definition(size = CellSize.ShrinkToFit)
        )
        val rows = listOf(
            BaseRow(definitions, "", "", "1"),
            BaseRow(definitions, "".span(2), "12")
        )
        references = rows.buildColumnReferencesFromDefinitions(definitions, 14)
        assertEquals(3, references.size)
        verify(0, 0, 4, 4)
        verify(1, 7, 9, 2)
        verify(2, 12, 14, 2)
    }

    @Test
    fun buildColumnReferenceWithShrinkToFitTest() {
        val definitions = listOf(
            Column.Definition(size = CellSize.EquallySpacing()),
            Column.Definition(size = CellSize.ShrinkToFit),
            Column.Definition(size = CellSize.ShrinkToFit)
        )
        val rows = listOf(
            BaseRow(definitions, "", "12", "123\n12"),
            BaseRow(definitions, "", "1\n0", "1234")
        )
        references = rows.buildColumnReferencesFromDefinitions(definitions, 14)
        assertEquals(3, references.size)
        verify(0, 0, 2, 2)
        verify(1, 5, 9, 4)
    }

    @Test
    fun columnReferenceShouldFillMaxBoundary() {
        val definitions = listOf(
            Column.Definition(),
            Column.Definition(),
            Column.Definition(alignment = Alignment.End, size = CellSize.ShrinkToFit),
            Column.Definition(alignment = Alignment.End, size = CellSize.ShrinkToFit)
        )
        val rows = listOf(
            BaseRow(definitions, "Hot Dog".span(3), "$2.25"),
            BaseRow(definitions, "Item Count:", 5, "Subtotal:", "$33.98"),
            BaseRow(definitions, "Receipt".end, 22317, "Total:".end, "$33.98"),
            BaseRow(definitions, "Cash:".span(3).align(Alignment.End), "-$16.02")
        )
        references = rows.buildColumnReferencesFromDefinitions(definitions, 48)
        assertEquals(4, references.size)
        verify(0, 0, 11, 11)
        verify(1, 14, 25, 11)
        verify(2, 28, 37, 9)
        verify(3, 42, 48, 8)
    }

    private fun verify(position: Int, start: Int, end: Int, len: Int) {
        assertEquals(start, references[position].start)
        assertEquals(end, references[position].end)
        assertEquals(len, references[position].len)
    }

    @Suppress("TestFunctionName")
    private fun BaseRow(definition: List<Column.Definition>, vararg items: Any): BaseRow = BaseRow(
        elements = items.toList(),
        definitions = definition
    )

}
