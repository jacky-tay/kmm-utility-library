package kmm.jacky.utilitylibrary.models.row

class SpacerRow(internal val weight: Int) : IRow {
    override fun toDisplayString(): List<String> {
        return (0..weight).map { "" }
    }
}
