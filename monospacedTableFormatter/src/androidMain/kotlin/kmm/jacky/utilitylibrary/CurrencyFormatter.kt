package kmm.jacky.utilitylibrary

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import java.text.NumberFormat
import java.util.Currency

actual open class CurrencyFormatter actual constructor() {

     actual open fun toString(number: Number): String {
        val config = Resources.getSystem().configuration
        ConfigurationCompat.getLocales(config).get(0)?.let {
            instance.currency = Currency.getInstance(it)
        }
        return instance.format(number)
    }

    companion object {
        val instance: NumberFormat by lazy { NumberFormat.getCurrencyInstance() }
    }
}
