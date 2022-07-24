package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.extensions.buildRepeat
import kmm.jacky.utilitylibrary.models.column.Column

class DividerRow(val char: String) : IRow {
    override var width: Int = 0

    override fun toDisplayString(reference: List<Column.Reference>?): List<String> {
        return listOf(char.buildRepeat(width))
    }
}
