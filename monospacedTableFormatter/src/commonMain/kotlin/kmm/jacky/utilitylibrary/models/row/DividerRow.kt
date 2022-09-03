package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.extensions.buildRepeat
import kmm.jacky.utilitylibrary.models.column.Column

class DividerRow(override var width: Int = 0, val char: String) : IRow {
    override fun toDisplayString(reference: List<Column.Reference>?): List<String> {
        return listOf(char.buildRepeat(width))
    }
}
