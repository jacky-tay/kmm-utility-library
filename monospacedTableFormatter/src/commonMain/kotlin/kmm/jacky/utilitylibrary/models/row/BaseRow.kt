package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.enums.CellSize
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.enums.WordBreakPolicy
import kmm.jacky.utilitylibrary.extensions.buildColumnReferencesFromDefinitions
import kmm.jacky.utilitylibrary.extensions.buildContent
import kmm.jacky.utilitylibrary.extensions.buildPrefix
import kmm.jacky.utilitylibrary.extensions.buildRepeat
import kmm.jacky.utilitylibrary.extensions.insertColumnDefinition
import kmm.jacky.utilitylibrary.models.column.Cell
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.models.wrapper.DividerWrapper
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.wrapper.SpacerWrapper
import kmm.jacky.utilitylibrary.public.Formatters
import kotlin.reflect.KClass

internal class BaseRow(
    elements: List<Any>,
    private val policy: LineWrap = LineWrap.Normal(WordBreakPolicy.Hyphen),
    override var width: Int = 0,
    internal val definitions: List<Column.Definition>? = null,
    rowFormatters: Formatters? = null,
    formatter: ((KClass<*>) -> Any?)? = null
) : IRow {

    internal var columns: List<Cell>

    init {
        fun getFormatter(input: Any): Any? {
            val type = input::class
            return rowFormatters?.get(type) ?: formatter?.invoke(type)
        }

        var index = 0
        columns = elements.map { element ->
            when (element) {
                is CurrencyWrapper -> Cell(element.injectFormatter(::getFormatter))
                is DividerRow -> Cell(DividerWrapper(element.char))
                is SpacerRow -> Cell(SpacerWrapper).span(element.weight)
                is Cell -> element
                else -> Cell(element)
            }.also {
                it.index = index
                index += it.span
            }
        }
        definitions?.let { columns.insertColumnDefinition(it) }
    }

    override fun toDisplayString(reference: List<Column.Reference>?): List<String> {
        val references = reference ?: listOf(this).buildColumnReferencesFromDefinitions(
            columns.map { it.definition },
            width
        )

        val cells = columns.map {
            val ref = references[it.index]
            val boundary = references[it.index + it.span - 1].end - ref.start
            it.buildString(boundary, policy)
        }
        val maxHeight = cells.maxOf { it.size }

        return (0 until maxHeight).map { row ->
            var string = " ".buildRepeat(width)
            columns.forEachIndexed { index, cell ->
                val ref = references[cell.index]
                val boundary = references[cell.index + cell.span - 1].end - ref.start
                cell.definition.alignment.buildContent(cells[index], boundary, row, maxHeight)
                    ?.let {
                        val start = ref.start + it.prefix
                        val text = cells[index][it.row]
                        string = string.replaceRange(start, start + text.length, text)
                    }
            }
            string
        }
    }

    internal fun getColumnWidthAt(index: Int, size: CellSize): Int {
        val cell = columns.firstOrNull { it.index == index } ?: return 0
        if (size == CellSize.ShrinkToFit) {
            return cell.content.lines().maxBy { it.length }.length
        }
        return cell.content.length
    }
}
