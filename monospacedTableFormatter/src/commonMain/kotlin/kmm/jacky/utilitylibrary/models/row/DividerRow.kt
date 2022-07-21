package kmm.jacky.utilitylibrary.models.row

class DividerRow(val char: String): IRow {
    override fun toDisplayString(): List<String> {
        return listOf("")
    }
}
