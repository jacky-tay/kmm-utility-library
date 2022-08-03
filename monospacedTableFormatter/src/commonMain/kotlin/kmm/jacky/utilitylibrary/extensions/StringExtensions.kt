package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.models.column.Cell

val String.center: Cell
    get() = Cell(this, alignment = Alignment.Center)

val String.end: Cell
    get() = Cell(this, alignment = Alignment.End)

fun String.span(span: Int): Cell = Cell(this, span = span)

fun String.align(alignment: Alignment): Cell = Cell(this, alignment = alignment)

fun String.buildRepeat(width: Int): String {
    return (0..(width / length)).joinToString("") { this }.take(width)
}

internal fun String.findAllPunctuations(): List<MatchResult> {
    return "[\\p{Punct}]+".toRegex().findAll(this).toList()
}

internal fun String.findAllSpaces(): List<MatchResult> {
    return "[\\s]+".toRegex().findAll(this).toList()
}
