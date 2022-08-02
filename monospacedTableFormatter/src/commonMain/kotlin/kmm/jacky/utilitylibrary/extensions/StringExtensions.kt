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

internal fun String.process(pos: Int, boundary: Int, wordBreakPolicy: LineWrap.WordBreakPolicy, pointer0: Int, pointer1: Int) : Pair<String, Int> {
    var end = if ((pointer1 - pos) < boundary) pointer1 else pos + boundary
    val result = substring(pos until end)
    return Pair(result, pos + result.length + (if (end < length && this[end] == ' ') 1 else 0)) // plus 1 if its a space
}

internal fun String.findAllPunctuations(): List<MatchResult> {
    return "[\\p{Punct}]+".toRegex().findAll(this).toList()
}

internal fun String.findAllSpaces(): List<MatchResult> {
    return "[\\s]+".toRegex().findAll(this).toList()
}

internal fun String.wrapInBoundary(boundary: Int): List<String> {
    val result = mutableListOf<String>()
    val ranges = "[\\p{Punct}\\s]+".toRegex().findAll(this).toList()
    var pos = 0

    var i = 0
    while (i < ranges.size) {
        val sum = ranges[i].range.last - pos
        if (sum > boundary) {
            val loc = ranges[i - 1].range
            val end = when {
                (loc.last - pos) <= boundary -> loc.last
                (loc.first - pos) <= boundary -> loc.first
                else -> (pos + 10).also { i -= 1 }
            }
            val sub = substring(pos until end)
            result.add(sub)
            pos = end + 1
        } else {
            i += 1
        }
    }

    result.add(substring(pos until length))
    return result
}
