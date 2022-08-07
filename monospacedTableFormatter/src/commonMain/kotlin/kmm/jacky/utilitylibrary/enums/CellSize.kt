package kmm.jacky.utilitylibrary.enums

sealed interface CellSize {
    object Undefined: CellSize

    object ShrinkToFit : CellSize

    object ExpandToFit : CellSize

    class EquallySpacing(val weight: Int = 1) : CellSize

    class FixedWidth(val width: Int) : CellSize

    class Percentage(val factor: Double) : CellSize { // range between 0.0 ... 1.0
        init {
            if (!((0.0)..(1.0)).contains(factor))
                throw IllegalArgumentException("Factor should be range between 0.0 and 1.0 inclusively")
        }
    }

    fun update(size: CellSize): CellSize = when(this) {
        Undefined -> size
        else -> this
    }

    fun getWeightIfAvailable(): Int = when (this) {
        is EquallySpacing -> weight
        is ExpandToFit -> 1
        else -> 0
    }
}

