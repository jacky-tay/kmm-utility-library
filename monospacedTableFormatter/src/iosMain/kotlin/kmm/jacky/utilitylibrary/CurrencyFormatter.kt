package kmm.jacky.utilitylibrary

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual open class CurrencyFormatter actual constructor() {
    actual open fun toString(number: Number): String {
        return instance.stringFromNumber(NSNumber(number.toDouble())) ?: number.toString()
    }

    companion object {
        val instance = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
        }
    }
}
