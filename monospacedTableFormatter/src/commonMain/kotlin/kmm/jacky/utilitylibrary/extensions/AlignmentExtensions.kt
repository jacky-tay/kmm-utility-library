package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.JointedAlignments
import kotlin.math.ceil
import kotlin.math.max
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

internal fun Alignment.getRowIndexAfterOffset(
    inputSize: Int,
    row: Int,
    maxRow: Int
): Int {
    val diff = maxRow - inputSize
    val half = round(diff / 2.0).toInt()
    val vertical = vertical()
    return max(
        -1, when {
            vertical is Alignment.Top && row < inputSize -> row
            vertical is Alignment.CenterVertically && (0 until inputSize).contains(row - half) -> row - half
            vertical is Alignment.Bottom && (0 until inputSize).contains(row - diff) -> row - diff
            else -> -1
        }
    )
}

internal data class ContentOffset(
    val prefix: Int,
    val row: Int
)

internal fun Alignment.buildContent(
    input: List<String>,
    boundary: Int,
    row: Int,
    maxRow: Int
): ContentOffset? {
    val rowOffset = getRowIndexAfterOffset(input.size, row, maxRow)
    return if (rowOffset != -1) {
        ContentOffset(prefix = buildPrefix(input[rowOffset].length, boundary), row = rowOffset)
    } else null
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