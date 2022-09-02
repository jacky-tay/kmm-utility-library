package kmm.jacky.utilitylibrary.enums

sealed interface CellSize {
    /**
     * Undefined
     */
    object Undefined : CellSize

    /**
     * Shrink to fit
     */
    object ShrinkToFit : CellSize

    /**
     * Expand to fit
     */
    object ExpandToFit : CellSize

    /**
     * Equally spacing
     *
     * @property weight
     */
    class EquallySpacing(val weight: Int = 1) : CellSize { // weight must be greater than 1
        init {
            if (weight < 1)
                throw IllegalArgumentException("Weight must be greater than 0")
        }
    }

    /**
     * Fixed width
     *
     * @property width Int, must be greater than 0 characters
     */
    class FixedWidth(val width: Int) : CellSize { // width must be greater than 0
        init {
            if (width < 1)
                throw IllegalArgumentException("Width must not be smaller than 0 character")
        }
    }

    /**
     * Percentage
     *
     * @property factor
     */
    class Percentage(val factor: Double) : CellSize { // range between 0.0 ... 1.0
        init {
            if (factor < 0 || factor > 1)
                throw IllegalArgumentException("Factor should be range between 0.0 and 1.0 inclusively")
        }
    }
}
