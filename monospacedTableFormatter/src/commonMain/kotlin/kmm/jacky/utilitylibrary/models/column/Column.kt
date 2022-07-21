package kmm.jacky.utilitylibrary.models.column

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.CellSize

interface Column {
    var definition: Definition

    data class Definition(
        var alignment: Alignment = Alignment.Start,
        var size: CellSize = CellSize.EquallySpacing(),
        var formatter: Any? = null
    )

    data class Reference(
        var end: Int
    ) {
        var start: Int = 0
            set(value) {
                field = value
                end = value + len
            }

        var len: Int = 0
            set(value) {
                field = value
                end = start + value
            }
    }
}
