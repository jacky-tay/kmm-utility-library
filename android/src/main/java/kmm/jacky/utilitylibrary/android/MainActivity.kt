package kmm.jacky.utilitylibrary.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.extensions.center
import kmm.jacky.utilitylibrary.extensions.currency
import kmm.jacky.utilitylibrary.extensions.end
import kmm.jacky.utilitylibrary.extensions.span
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.public.TableFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TableFormatterPreview()
        }
    }
}

@Preview
@OptIn(ExperimentalUnitApi::class)
@Composable
fun TableFormatterPreview() {
    val table = TableFormatter(48, divider = "=") {
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
    }
    MaterialTheme {
        Text(
            table,
            fontSize = TextUnit(9.5f, TextUnitType.Sp),
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.scrollable(
                rememberScrollState(),
                orientation = Orientation.Vertical
            )
        )
    }
}