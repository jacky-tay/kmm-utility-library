package kmm.jacky.utilitylibrary.models.row

import kmm.jacky.utilitylibrary.extensions.insertColumnDefinition
import kmm.jacky.utilitylibrary.models.column.Cell
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.models.wrapper.DividerWrapper
import kmm.jacky.utilitylibrary.models.column.Column
import kmm.jacky.utilitylibrary.models.wrapper.SpacerWrapper
import kmm.jacky.utilitylibrary.public.Formatters
import kotlin.reflect.KClass

class BaseRow(
    elements: List<Any>,
    rowFormatters: Formatters? = null,
    formatter: ((KClass<*>) -> Any?)? = null
) : IRow {
//    private val _columns = mutableListOf<Cell>()

    var columns: List<Cell>
        private set
//        get() = _columns

    val totalCount: Int
        get() = columns.size

    val totalSpannableCount: Int
        get() = columns.fold(0) { sum, element ->
            sum + element.span
        }

    init {
        fun getFormatter(input: Any): Any? {
            val type = input::class
            return rowFormatters?.get(type) ?: formatter?.invoke(type)
        }

//        for (element in elements) {
//            // insert index
//            when (element) {
//                is CurrencyWrapper -> {
//                    val format = getFormatter(element)
//                    val value = element.toString(format)
//                    _columns.add(Cell(value))
//                }
//                is DividerRow -> _columns.add(Cell(DividerWrapper(element.char)))
//                is SpacerRow -> _columns.add(Cell(" ").span(element.weight))
//                is Cell -> _columns.add(element)
//                else -> _columns.add(Cell(element))
//            }
//            _columns.last().index = _columns.size - 1 // pre-assign index
//        }

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

    override fun toDisplayString(): List<String> {
        return emptyList()
    }

    internal fun getColumnWidthAt(index: Int): Int {
        val cell = columns.firstOrNull { it.index == index } ?: return 0
        return cell.content.length
    }
}
