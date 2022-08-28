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

class BaseRow(
    private val policy: LineWrap,
    elements: List<Any>,
    rowFormatters: Formatters? = null,
    formatter: ((KClass<*>) -> Any?)? = null
) : IRow {
    override var width: Int = 0

    var columns: List<Cell>
        private set

    internal constructor(
        vararg elements: Any
    ) : this(LineWrap.Normal(WordBreakPolicy.Hyphen), elements.toList())

    init {
        fun getFormatter(input: Any): Any? {
            val type = input::class
            return rowFormatters?.get(type) ?: formatter?.invoke(type)
        }

        columns = elements.mapIndexed { index, element ->
            when (element) {
                is CurrencyWrapper -> Cell(element.injectFormatter(::getFormatter))
                is DividerRow -> Cell(DividerWrapper(element.char))
                is SpacerRow -> Cell(SpacerWrapper).span(element.weight)
                is Cell -> element
                else -> Cell(element)
            }.also { it.index = index }
        }
    }

    fun insertColumnDefinition(definitions: List<Column.Definition>) {
        columns.insertColumnDefinition(definitions)
    }

    override fun toDisplayString(reference: List<Column.Reference>?): List<String> {
        val references = reference ?: listOf(this).buildColumnReferencesFromDefinitions(
            columns.map { it.definition },
            width
        )

        val cells = columns.map { it.buildString(references[it.index].len, policy) }
        val maxHeight = cells.maxOf { it.size }

        return (0 until maxHeight).map { row ->
            var string = " ".buildRepeat(width)
            columns.forEachIndexed { index, cell ->
                val ref = references[index]
                cell.definition.alignment.buildContent(cells[index], ref.len, row, maxHeight)?.let {
                    val start = ref.start + it.first
                    val text = cells[index][it.second]
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
