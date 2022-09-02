package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.enums.WordBreakPolicy
import kmm.jacky.utilitylibrary.extensions.center
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kotlin.test.Test
import kotlin.test.assertEquals

class BaseRowTests {

    @Test
    fun testBasicBaseRow() {
        val row = BaseRow(
            width = 22,
            policy = LineWrap.Normal(WordBreakPolicy.Hyphen),
            elements = listOf("hello", "world", "10")
        )
        row.insertColumnDefinition(
            listOf(
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ShrinkToFit)
            )
        )
        val actual = row.toDisplayString(null)
        val expect = listOf("hello     world     10")
        assertEquals(expect, actual)
    }

    @Test
    fun testBaseRowCenterAlignment() {
        val row = BaseRow(
            width = 22,
            policy = LineWrap.Normal(WordBreakPolicy.Hyphen),
            elements = listOf("hello", "hello world !!", "10")
        )
        row.insertColumnDefinition(
            listOf(
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ShrinkToFit, alignment = Alignment.Center)
            )
        )
        val actual = row.toDisplayString(null)
        val expect = listOf("hello     hello       ", "          world     10", "          !!          ")
        assertEquals(expect, actual)
    }

    @Test
    fun testBaseRowBottomAlignment() {
        val row = BaseRow(
            width = 22,
            policy = LineWrap.Normal(WordBreakPolicy.Hyphen),
            elements = listOf("hello", "world !!", "10")
        )
        row.insertColumnDefinition(
            listOf(
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ExpandToFit),
                Column.Definition(size = CellSize.ShrinkToFit, alignment = Alignment.Bottom)
            )
        )
        val actual = row.toDisplayString(null)
        val expect = listOf("hello     world       ", "          !!        10")
        assertEquals(expect, actual)
    }

    @Test
    fun testBaseRowWithCenterAlignment() {
        val row = BaseRow(
            "Brandy's General Store\n321 Any Street\nAnytown, NY 10121\n(212) 555-5555".center
        )
        row.width = 22
        val actual = row.toDisplayString(null)
        val expect = listOf("Brandy's General Store", "    321 Any Street    ", "   Anytown, NY 10121  ", "    (212) 555-5555    ")
        assertEquals(expect, actual)
    }
}
