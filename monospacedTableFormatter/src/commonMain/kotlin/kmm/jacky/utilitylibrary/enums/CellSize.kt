package kmm.jacky.utilitylibrary.enums

sealed interface CellSize {
    object ShrinkToFit : CellSize

    object ExpandToFit : CellSize

    class EquallySpacing(val weight: Int = 1) : CellSize

    class FixedWidth(val width: Int) : CellSize

    class Percentage(val factor: Double) : CellSize // range between 0.0 ... 1.0

    fun getWeightIfAvailable(): Int = when (this) {
        is EquallySpacing -> weight
        is ExpandToFit -> 1
        else -> 0
    }
}

