package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.JointedAlignments
import kotlin.math.ceil
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

internal fun Alignment.buildPrefix(stringLen: Int, boundary: Int): Int {
    if (stringLen == boundary) return 0
    else if (stringLen > boundary) throw IndexOutOfBoundsException()

    return when (horizontal()) {
        is Alignment.CenterHorizontally -> ceil((boundary - stringLen) / 2.0).toInt()
        is Alignment.End -> boundary - stringLen
        else -> 0
    }
}

internal fun Alignment.buildContent(
    input: List<String>,
    boundary: Int,
    row: Int,
    maxRow: Int
): Pair<Int, Int>? {
    val vertical = vertical()
    val diff = maxRow - input.size
    return when {
        vertical is Alignment.Top && row < input.size -> Pair(
            buildPrefix(
                input[row].length,
                boundary
            ), row
        )
        vertical is Alignment.CenterVertically -> {
            val index = maxRow - row - input.size
            val half = round(diff / 2.0).toInt()
            null
        }
        vertical is Alignment.Bottom -> {
            val index = row + input.size - maxRow
            if (index >= 0) Pair(buildPrefix(input[index].length, boundary), index) else null
        }
        else -> null
    }
}

//
//internal fun Alignment.buildContent(input: List<String>, boundary: Int, maxRow: Int): List<String> {
//    val shiftedHorizontally = input.map { this.buildString(it, boundary) }
//    if (input.size == maxRow) return shiftedHorizontally
//    else if (input.size > maxRow) throw IndexOutOfBoundsException()
//
//    val vertical = vertical()
//    val result = shiftedHorizontally.toMutableList()
//    val template = " ".buildRepeat(boundary)
//    val diff = maxRow - input.size
//    when (vertical) {
//        is Alignment.Top -> repeat(diff) { result.add(template) }
//        is Alignment.CenterVertically -> {
//            val half = round(diff / 2.0).toInt()
//            repeat(half) { result.add(0, template) }
//            val remaining = maxRow - result.size
//            repeat(remaining) { result.add(template) }
//        }
//        is Alignment.Bottom -> repeat(maxRow - input.size) {
//            result.add(0, template)
//        }
//        else -> Unit
//    }
//    return result
//}