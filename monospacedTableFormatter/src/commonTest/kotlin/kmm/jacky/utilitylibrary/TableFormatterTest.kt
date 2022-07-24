package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.extensions.*
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kmm.jacky.utilitylibrary.public.Formatters
import kmm.jacky.utilitylibrary.public.TableFormatter
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class TableFormatterTest {

    @Test
    fun testExample() {
        val currencyFormatter = object : CurrencyFormatter() {
            override fun toString(number: Number): String {
                val sign = if (number.isNegativeValue) "-" else ""
                return "$sign\$${abs(number.toDouble())}"
            }
        }

        val formatter: Formatters = mapOf(
            CurrencyWrapper::class to currencyFormatter
        )

        val table = TableFormatter(48, divider = "=", formatters = formatter) {
            Row("Brandy's General Store\n321 Any Street\nAnytown, NY 10121\n(212) 555-5555".center)
            Divider()
            Row("Friday, Dec 17, 2013 12:04 PM 14792 Bradley J")
            Divider()
            CellDefinition(
                Column.Definition(),
                Column.Definition(),
                Column.Definition(alignment = Alignment.End, size = CellSize.ShrinkToFit),
                Column.Definition(alignment = Alignment.End, size = CellSize.ShrinkToFit)
            )
            Row("Hot Dog".span(3), 2.25.currency)
            Row("Egg Roll".span(3), 2.currency)
            Row("Hot Pretzel".span(3), 1.75.currency)
            Row("Cheese Danish".span(3), 2.99.currency)
            Row("Jersey, Grey XL".span(3), 24.99.currency)
            Divider()
            Row("Item Count:", 5, "Subtotal:", 33.98.currency)
            Row("Sales Tax:".end.span(3), 33.98.currency)
            Row("".span(2), Divider("="))
            Row("Receipt".end, 22317, "Total:".end, 33.98.currency)
            Spacer()
            Row("Cash:".span(3).align(Alignment.End), 50.currency)
            Row("Cash:".span(3).align(Alignment.End), (-16.02).currency)
            Spacer()
            Divider()
            Row("Thank you for Shopping at Brandy's!".center)

            assertEquals(20, rows.size)
            assertEquals(1, (rows[2] as BaseRow).columns.size)

            assertEquals(2, (rows[4] as BaseRow).columns.size)
            assertEquals(3, (rows[4] as BaseRow).columns[0].span)
            assertEquals(Alignment.End, (rows[4] as BaseRow).columns[1].definition.alignment)
            assertEquals(CellSize.ShrinkToFit, (rows[4] as BaseRow).columns[1].definition.size)
            assertEquals("$2.25", (rows[4] as BaseRow).columns[1].content)

            assertEquals(4, (rows[10] as BaseRow).columns.size)

            assertEquals(1, (rows[19] as BaseRow).columns.size)
            assertEquals(4, (rows[19] as BaseRow).columns[0].span)
        }

    }
}
