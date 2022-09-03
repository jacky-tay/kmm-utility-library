package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.models.column.Column

class SpacerRow(override var width: Int = 0, internal val weight: Int) : IRow {

    override fun toDisplayString(reference: List<Column.Reference>?): List<String> {
        return (0 until weight).map { "" }
    }
}
