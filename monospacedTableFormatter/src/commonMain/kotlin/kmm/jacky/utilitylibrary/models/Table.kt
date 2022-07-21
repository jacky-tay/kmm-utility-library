package kmm.jacky.utilitylibrary.models

import kmm.jacky.utilitylibrary.extensions.buildColumnReferences
import kmm.jacky.utilitylibrary.extensions.isValid
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.DividerRow
import kmm.jacky.utilitylibrary.models.row.IRow
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kmm.jacky.utilitylibrary.models.row.SpacerRow
import kmm.jacky.utilitylibrary.public.Formatters

class Table internal constructor(
    val width: Int,
    private val divider: String = "-",
    private val formatters: Formatters
) {
    private val _rows = mutableListOf<IRow>()
    internal val rows: List<IRow>
        get() = _rows

    private val definitions = mutableListOf<Pair<Int, List<Column.Definition>>>()

    private inline fun <reified T> filter(input: MutableList<Any>) {
        if (input.any { it is T }) {
            val index = _rows.indexOfLast { it is T }
            if ((_rows.size - index) < input.size) {
                _rows.removeAt(index)
            }
        }
    }

    private fun processRowElements(elements: Array<out Any>): List<Any> {
        val filtered = elements.filterNot { it is Unit }.toMutableList()
        filter<DividerRow>(filtered)
        filter<SpacerRow>(filtered)
        return filtered
    }

    @Suppress("FunctionName")
    fun Row(vararg elements: Any) {
        // ensuring no void function is included
        val filtered = processRowElements(elements)

        val row = BaseRow(filtered, rowFormatters = null) {
            formatters[it]
        }

        val definition = getDefinition(_rows.size)
        if (definition != null) {
            row.insertColumnDefinition(definition)
        }
        _rows.add(row)
    }

    @Suppress("FunctionName")
    fun Divider(char: String = divider): DividerRow {
//        val definition = getDefinition(_rows.size)
//        val divider = Cell(char)
//        if (definition != null) {
//            divider.span(definition.size)
//        }
        val row = DividerRow(char)
        _rows.add(row)
        return row
    }

    @Suppress("FunctionName")
    fun CellDefinition(vararg definition: Column.Definition) {
        val list = definition.toList()
        if (!list.isValid(width)) return
        definitions.add(Pair(_rows.size - 1, list))
    }

    @Suppress("FunctionName")
    fun Spacer(weight: Int = 1): SpacerRow {
        val row = SpacerRow(weight)
        _rows.add(row)
        return row
    }

    internal fun getDefinition(row: Int): List<Column.Definition>? {
        if (definitions.isEmpty()) return null
        return definitions.lastOrNull { it.first < row }?.second
    }

    internal fun buildColumnReference() {
        val firstDefinition = definitions.firstOrNull()
        if (firstDefinition != null && firstDefinition.first > 0) {
            // loop before
            rows.subList(0, firstDefinition.first).buildColumnReferences(firstDefinition.second, width)
        }
        for (definition in definitions) {

        }
        if (definitions.lastOrNull() != null) {

        }
    }

    override fun toString(): String {
        return _rows.flatMap { it.toDisplayString() }.joinToString("\n")
    }
}
