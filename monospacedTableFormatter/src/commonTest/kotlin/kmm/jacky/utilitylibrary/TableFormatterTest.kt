package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.extensions.*
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.public.Formatters
import kmm.jacky.utilitylibrary.public.TableFormatter
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals

class TableFormatterTest {
    private val currencyFormatter = object : CurrencyFormatter() {
        override fun toString(number: Number): String {
            val sign = if (number.isNegativeValue) "-" else ""
            var string = "$sign\$${abs(number.toDouble())}"
            val diff = 3 + string.indexOfLast { it == '.' } - string.length
            if (diff > 0)
                string += ("0".buildRepeat(diff))
            return string
        }
    }
    private val formatter: Formatters = mapOf(
        CurrencyWrapper::class to currencyFormatter
    )


    @Test
    fun testExample() {
        val table = TableFormatter(48, divider = "=", formatters = formatter) {
            Row("Brandy's General Store\n321 Any Street\nAnytown, NY 10121\n(212) 555-5555".center)
            DividerRow()
            Row("Friday, Dec 17, 2013 12:04 PM 14792 Bradley J".center)
            DividerRow()
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
            DividerRow()
            Row("Item Count:", 5, "Subtotal:", 33.98.currency)
            Row(Spacer(2), "Sales Tax:", 33.98.currency)
            Row(Spacer().span(2), Divider("=", span = 2))
            Row("Receipt".end, 22317, "Total:".end, 33.98.currency)
            SpacerRow()
            Row("Cash:".span(3).align(Alignment.End), 50.currency)
            Row("Cash:".span(3).align(Alignment.End), (-16.02).currency)
            SpacerRow()
            DividerRow()
            Row("Thank you for Shopping at Brandy's!".center)

            assertEquals(20, rows.size)
            assertEquals(1, (rows[2] as BaseRow).columns.size)

            assertEquals(1, definitions.size)

            assertEquals(2, (rows[4] as BaseRow).columns.size)
            assertEquals(3, (rows[4] as BaseRow).columns[0].span)
            assertEquals(Alignment.End, (rows[4] as BaseRow).columns[1].definition.alignment)
            assertEquals(CellSize.ShrinkToFit, (rows[4] as BaseRow).columns[1].definition.size)
            assertEquals("$2.25", (rows[4] as BaseRow).columns[1].content)

            assertEquals(4, (rows[10] as BaseRow).columns.size)

            assertEquals(1, (rows[19] as BaseRow).columns.size)
            assertEquals(4, (rows[19] as BaseRow).columns[0].span)
        }
        val expect = """
             Brandy's General Store             
                 321 Any Street                 
                Anytown, NY 10121               
                 (212) 555-5555                 
================================================
  Friday, Dec 17, 2013 12:04 PM 14792 Bradley J 
================================================
Hot Dog                                    $2.25
Egg Roll                                   $2.00
Hot Pretzel                                $1.75
Cheese Danish                              $2.99
Jersey, Grey XL                           $24.99
================================================
Item Count:   5              Subtotal:    $33.98
                            Sales Tax:    $33.98
                            ====================
Receipt       22317             Total:    $33.98

                                 Cash:    $50.00
                                 Cash:   -$16.02

================================================
       Thank you for Shopping at Brandy's!      
        """.trimIndent()
        assertEquals(expect, table)
    }
}
