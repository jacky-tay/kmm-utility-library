package kmm.jacky.utilitylibrary.models.wrapper

import kmm.jacky.utilitylibrary.CurrencyFormatter

data class CurrencyWrapper(
    val value: Number
) {
    fun injectFormatter(formatter: (Any) -> (Any?)): String = toString(formatter(this))

    fun toString(formatter: Any?): String =
        (formatter as? CurrencyFormatter)?.toString(value) ?: value.toString()
}
