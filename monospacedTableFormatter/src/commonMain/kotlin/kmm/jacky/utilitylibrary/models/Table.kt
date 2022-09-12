package kmm.jacky.utilitylibrary.models

import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.extensions.buildColumnReferencesFromDefinitions
import kmm.jacky.utilitylibrary.extensions.isValid
import kmm.jacky.utilitylibrary.models.column.Cell
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.row.DividerRow
import kmm.jacky.utilitylibrary.models.row.IRow
import kmm.jacky.utilitylibrary.models.row.BaseRow
import kmm.jacky.utilitylibrary.models.row.RowDefinition
import kmm.jacky.utilitylibrary.models.row.SpacerRow
import kmm.jacky.utilitylibrary.models.wrapper.DividerWrapper
import kmm.jacky.utilitylibrary.models.wrapper.SpacerWrapper
import kmm.jacky.utilitylibrary.public.Formatters

class Table internal constructor(
    private val width: Int,
    private val tablePolicy: LineWrap,
    private val divider: String = "-",
    private val formatters: Formatters
) {
    private val _rows = mutableListOf<IRow>()
    internal val rows: List<IRow>
        get() = _rows
    internal val definitions = mutableListOf<RowDefinition>()

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
    fun Row(vararg elements: Any, policy: LineWrap = tablePolicy) {
        // ensuring no void function is included
        val filtered = processRowElements(elements)

        // add definition to row elements
        val definition = getDefinition(_rows.size)
        val row = BaseRow(
            width = width,
            policy = policy,
            elements = filtered,
            definitions = definition,
            rowFormatters = null,
        ) {
            formatters[it]
        }

        _rows.add(row)
    }

    @Suppress("FunctionName")
    fun DividerRow(char: String = divider): DividerRow {
        val row = DividerRow(width, char)
        _rows.add(row)
        return row
    }

    @Suppress("FunctionName")
    fun Divider(char: String = divider, span: Int = 1): Cell {
        return Cell(DividerWrapper(char), span = span)
    }

    @Suppress("FunctionName")
    fun CellDefinition(vararg definition: Column.Definition) {
        val list = definition.toList()
        if (!list.isValid(width)) return
        definitions.add(RowDefinition(_rows.size - 1, list))
    }

    @Suppress("FunctionName")
    fun SpacerRow(row: Int = 1): SpacerRow {
        val row = SpacerRow(width, row)
        _rows.add(row)
        return row
    }

    @Suppress("FunctionName")
    fun Spacer(span: Int = 1): Cell {
        return Cell(SpacerWrapper, span = span)
    }

    internal fun getDefinition(row: Int): List<Column.Definition>? {
        if (definitions.isEmpty()) return null
        return definitions.lastOrNull { it.row < row }?.definitions
    }

    internal fun buildColumnReference() {
        definitions.forEach { rowDefinition ->
            val filteredRow =
                rows.filter { (it as? BaseRow)?.definitions.hashCode() == rowDefinition.definitions.hashCode() }
            rowDefinition.references =
                filteredRow.buildColumnReferencesFromDefinitions(rowDefinition.definitions, width)
        }
    }

    override fun toString(): String {
        return rows.flatMapIndexed { index: Int, iRow: IRow ->
            iRow.toDisplayString(getColumnReference(index))
        }.joinToString("\n")
    }

    internal fun getColumnReference(row: Int): List<Column.Reference>? {
        return definitions.lastOrNull { it.row < row }?.references
    }
}
