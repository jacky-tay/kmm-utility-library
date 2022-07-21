package kmm.jacky.utilitylibrary.public

import kmm.jacky.utilitylibrary.CurrencyFormatter
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.models.Table
import kotlin.reflect.KClass

typealias Formatters = Map<KClass<*>, Any>

@Suppress("FunctionName")
fun TableFormatter(
    width: Int,
    divider: String = "-",
    formatters: Formatters = DefaultFormatter(),
    block: Table.() -> Unit
): String {
    val table = Table(width = width, divider = divider, formatters = formatters)
    block(table)
    table.buildColumnReference()
    return table.toString()
}

@Suppress("FunctionName")
fun DefaultFormatter() = mutableMapOf<KClass<*>, Any>(
    CurrencyWrapper::class to CurrencyFormatter()
)
