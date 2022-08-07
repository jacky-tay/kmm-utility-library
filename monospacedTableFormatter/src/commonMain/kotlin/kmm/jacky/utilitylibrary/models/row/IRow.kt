package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.models.column.Column

interface IRow {
    var width: Int
    fun toDisplayString(reference: List<Column.Reference>?): List<String>
}
