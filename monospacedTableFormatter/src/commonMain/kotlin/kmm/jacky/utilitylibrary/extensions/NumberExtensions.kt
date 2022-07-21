package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.models.column.Cell
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper

val Number.isNegativeValue: Boolean
    get() = when (this) {
        is Double -> this < 0
        is Int -> this < 0
        is Float -> this < 0
        is Long -> this < 0
        is Short -> this < 0
        else -> false
    }

val Number.center: Cell
    get() = Cell(this, alignment = Alignment.Center)

val Number.end: Cell
    get() = Cell(this, alignment = Alignment.End)

fun Number.span(span: Int): Cell = Cell(this, span = span)

fun Number.align(alignment: Alignment): Cell = Cell(this, alignment = alignment)

val Number.currency: CurrencyWrapper
    get() = CurrencyWrapper(this)

