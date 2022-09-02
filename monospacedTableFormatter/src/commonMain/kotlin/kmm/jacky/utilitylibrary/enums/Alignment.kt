package kmm.jacky.utilitylibrary.enums

import kmm.jacky.utilitylibrary.extensions.isHorizontal
import kmm.jacky.utilitylibrary.extensions.isVertical

sealed interface Alignment {
    object Start : Alignment

    object Center : Alignment {
        override fun shiftIfNeededWhenJoinedWith(alignment: Alignment): Alignment {
            return when {
                alignment.isHorizontal() -> CenterVertically
                alignment.isVertical() -> CenterHorizontally
                else -> this
            }
        }
    }

    object CenterHorizontally : Alignment

    object CenterVertically : Alignment

    object End : Alignment

    object Top : Alignment

    object Bottom : Alignment

    object Undefined : Alignment

    fun shiftIfNeededWhenJoinedWith(alignment: Alignment) = this
}

internal class JointedAlignments(vararg alignment: Alignment) : Alignment {
    internal val alignments: List<Alignment> = alignment.toList()

    fun contains(alignment: Alignment): Boolean = alignments.contains(alignment)
}
