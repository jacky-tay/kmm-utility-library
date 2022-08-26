package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.JointedAlignments
import kotlin.math.ceil

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
