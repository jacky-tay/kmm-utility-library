package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.JointedAlignments
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

internal fun Alignment.horizontal(): Alignment = when {
    this is Alignment.Center -> Alignment.CenterHorizontally
    isHorizontal() -> this
    this is JointedAlignments -> alignments.first { it.isHorizontal() }.horizontal()
    else -> Alignment.Start
}

internal fun Alignment.vertical(): Alignment = when {
    this is Alignment.Center -> Alignment.CenterVertically
    isVertical() -> this
    this is JointedAlignments -> alignments.first { it.isVertical() }.vertical()
    else -> Alignment.Top
}

internal fun Alignment.isHorizontal(): Boolean = when (this) {
    Alignment.Start, Alignment.CenterHorizontally, Alignment.End, Alignment.Center -> true
    else -> false
}

internal fun Alignment.isVertical(): Boolean = when (this) {
    Alignment.Top, Alignment.CenterVertically, Alignment.Bottom, Alignment.Center -> true
    else -> false
}

internal fun Alignment.isCenter(): Boolean = when (this) {
    Alignment.Center, Alignment.CenterHorizontally, Alignment.CenterVertically -> true
    is JointedAlignments -> alignments.any { it.isCenter() }
    else -> false
}

internal fun Alignment.canJoinWith(other: Alignment): Boolean = when {
    this is Alignment.Undefined || other is Alignment.Undefined -> false
    this is Alignment.Center && other is Alignment.Center -> false
    isHorizontal() && other.isVertical() -> true
    isVertical() && other.isHorizontal() -> true
    else -> false
}

internal fun Alignment.update(alignment: Alignment): Alignment = when (this) {
    Alignment.Undefined -> alignment
    else -> this
}

operator fun Alignment.plus(alignment: Alignment): Alignment {
    if (canJoinWith(alignment)) {
        return JointedAlignments(
            this.shiftIfNeededWhenJoinedWith(alignment),
            alignment.shiftIfNeededWhenJoinedWith(this)
        )
    }
    throw UnsupportedOperationException("$this and $alignment cannot be joint")
}

internal fun Alignment.buildString(string: String, boundary: Int): String {
    if (string.length == boundary) return string
    else if (string.length > boundary) throw IndexOutOfBoundsException()

    val horizontal = horizontal()
    val stringBuilder = StringBuilder()
    val prefix: Int = when (horizontal) {
        is Alignment.CenterHorizontally -> ceil((boundary - string.length) / 2.0).toInt()
        is Alignment.End -> boundary - string.length
        else -> 0
    }
    stringBuilder.append(" ".buildRepeat(prefix))
    stringBuilder.append(string)
    val suffix: Int = when (horizontal) {
        is Alignment.Start -> boundary - string.length
        is Alignment.CenterHorizontally -> boundary - string.length - prefix
        else -> 0
    }
    stringBuilder.append(" ".buildRepeat(suffix))
    return stringBuilder.toString()
}

internal fun Alignment.buildContent(input: List<String>, boundary: Int, maxRow: Int): List<String> {
    val shiftedHorizontally = input.map { this.buildString(it, boundary) }
    if (input.size == maxRow) return shiftedHorizontally
    else if (input.size > maxRow) throw IndexOutOfBoundsException()

    val vertical = vertical()
    val result = shiftedHorizontally.toMutableList()
    val template = " ".buildRepeat(boundary)
    val diff = maxRow - input.size
    when (vertical) {
        is Alignment.Top -> repeat(diff) { result.add(template) }
        is Alignment.CenterVertically -> {
            val half = round(diff / 2.0).toInt()
            repeat(half) { result.add(0, template) }
            val remaining = maxRow - result.size
            repeat(remaining) { result.add(template) }
        }
        is Alignment.Bottom -> repeat(maxRow - input.size) {
            result.add(0, template)
        }
        else -> Unit
    }
    return result
}