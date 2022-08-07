package kmm.jacky.utilitylibrary.extensions

import kmm.jacky.utilitylibrary.enums.CellSize

fun CellSize.update(size: CellSize): CellSize = when (this) {
    CellSize.Undefined -> size
    else -> this
}

fun CellSize.getWeightIfAvailable(): Int = when (this) {
    is CellSize.EquallySpacing -> weight
    is CellSize.ExpandToFit -> 1
    else -> 0
}

