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

internal fun String.process(
    pos: Int,
    boundary: Int,
    wordBreakPolicy: LineWrap.WordBreakPolicy,
    pointer0: Int,
    pointer1: Int
): Pair<String, Int> {
    val wordWontFit = if (wordBreakPolicy == LineWrap.WordBreakPolicy.Hyphen)
        substring(pointer0 until pointer1)
    else
        ""

    val shouldBreakWord = wordBreakPolicy == LineWrap.WordBreakPolicy.Hyphen &&
            wordWontFit.length > boundary

    val punctuation = if (shouldBreakWord)
        wordWontFit.findAllPunctuations().lastOrNull { it.range.first <= boundary }
    else
        null

    val shouldTruncate = shouldBreakWord &&
            (boundary - pointer0 + pos) > 3 && // can fit at least 3 characters
            pos <= pointer0
    val shouldAddTruncatedHyphen = shouldTruncate && punctuation == null
    val end = when {
        shouldTruncate && punctuation != null ->
            pos + punctuation.range.first // break at any punctuation
        shouldTruncate -> pos + boundary - wordBreakPolicy.text.length
        shouldBreakWord && pos < pointer0 -> pointer0
        (pointer1 - pos) < boundary -> pointer1
        else -> pos + boundary
    }

    val result = substring(pos until end) + (if (shouldAddTruncatedHyphen) wordBreakPolicy.text else "")
    val offset = when {
        shouldAddTruncatedHyphen -> -1 // -1 if its truncated with hyphen
        end < length && this[end] == ' ' -> 1 // add offset 1 if its a space
        else -> 0
    }
    return Pair(result, pos + result.length + offset)
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
