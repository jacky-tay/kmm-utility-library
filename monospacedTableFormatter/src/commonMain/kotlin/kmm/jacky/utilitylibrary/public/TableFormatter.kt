package kmm.jacky.utilitylibrary.public

import kmm.jacky.utilitylibrary.CurrencyFormatter
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.enums.WordBreakPolicy
import kmm.jacky.utilitylibrary.models.wrapper.CurrencyWrapper
import kmm.jacky.utilitylibrary.models.Table
import kotlin.reflect.KClass

typealias Formatters = Map<KClass<*>, Any>

@Suppress("FunctionName")
fun TableFormatter(
    width: Int,
    policy: LineWrap = LineWrap.Normal(WordBreakPolicy.Hyphen),
    divider: String = "-",
    formatters: Formatters? = null,
    block: Table.() -> Unit
): String {
    val table = Table(
        width = width,
        tablePolicy = policy,
        divider = divider,
        formatters = DefaultFormatter() + (formatters ?: emptyMap())
    )
    block(table)
    table.buildColumnReference()
    return table.toString()
}

@Suppress("FunctionName")
fun DefaultFormatter() = mapOf<KClass<*>, Any>(
    CurrencyWrapper::class to CurrencyFormatter(),
)
