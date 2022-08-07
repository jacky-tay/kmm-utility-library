package kmm.jacky.utilitylibrary.enums

sealed class Alignment {
    object Start : Alignment()

    object Center : Alignment() {
        override fun shiftIfNeededWhenJoinedWith(alignment: Alignment): Alignment {
            return when {
                alignment.isHorizontal() -> CenterVertically
                alignment.isVertical() -> CenterHorizontally
                else -> this
            }
        }
    }

    object CenterHorizontally : Alignment()

    object CenterVertically : Alignment()

    object End : Alignment()

    object Top : Alignment()

    object Bottom : Alignment()

    object Undefined : Alignment()

    // region Internal methods
    internal class Jointed(vararg alignment: Alignment) : Alignment() {
        internal val alignments: List<Alignment> = alignment.toList()

        fun contains(alignment: Alignment): Boolean = alignments.contains(alignment)
    }

    internal fun horizontal(): Alignment = when {
        this is Center -> CenterHorizontally
        isHorizontal() -> this
        this is Jointed -> alignments.first { it.isHorizontal() }.horizontal()
        else -> Start
    }

    internal fun vertical(): Alignment = when {
        this is Center -> CenterVertically
        isVertical() -> this
        this is Jointed -> alignments.first { it.isVertical() }.vertical()
        else -> Top
    }

    internal fun isHorizontal(): Boolean = when (this) {
        Start, CenterHorizontally, End, Center -> true
        is Jointed -> alignments.any { it.isHorizontal() }
        else -> false
    }

    internal fun isVertical(): Boolean = when (this) {
        Top, CenterVertically, Bottom, Center -> true
        is Jointed -> alignments.any { it.isVertical() }
        else -> false
    }

    internal fun isCenter(): Boolean = when (this) {
        Center, CenterHorizontally, CenterVertically -> true
        is Jointed -> alignments.any { it.isCenter() }
        else -> false
    }

    internal fun canJoinWith(other: Alignment): Boolean = when {
        this is Undefined || other is Undefined -> false
        this is Center && other is Center -> false
        isHorizontal() && other.isVertical() -> true
        isVertical() && other.isHorizontal() -> true
        else -> false
    }

    internal open fun shiftIfNeededWhenJoinedWith(alignment: Alignment) = this

    internal fun update(alignment: Alignment): Alignment = when (this) {
        Undefined -> alignment
        else -> this
    }
    // endregion

    operator fun plus(alignment: Alignment): Alignment {
        if (canJoinWith(alignment)) {
            return Jointed(
                this.shiftIfNeededWhenJoinedWith(alignment),
                alignment.shiftIfNeededWhenJoinedWith(this)
            )
        }
        throw UnsupportedOperationException("$this and $alignment cannot be joint")
    }

}
