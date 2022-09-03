package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.models.column.Column

data class RowDefinition(
    val row: Int,
    val definitions: List<Column.Definition>
) {
    lateinit var references: List<Column.Reference>
}