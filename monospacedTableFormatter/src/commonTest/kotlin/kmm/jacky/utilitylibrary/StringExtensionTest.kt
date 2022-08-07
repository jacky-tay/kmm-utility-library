package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.extensions.buildRepeat
import kotlin.test.Test
import kotlin.test.assertEquals

class StringExtensionTest {

    @Test
    fun testRepeater() {
        assertEquals("-----", "-".buildRepeat(5))
        assertEquals("-=-=-", "-=".buildRepeat(5))
        assertEquals("<<..>", "<<..>>".buildRepeat(5))
    }
}
