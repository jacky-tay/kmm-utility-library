package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.enums.LineWrap.WordBreakPolicy.Hyphen
import kmm.jacky.utilitylibrary.enums.LineWrap.WordBreakPolicy.None
import kotlin.test.Test
import kotlin.test.assertEquals

class LineWrapTest {

    private val input = "Hello world!"

    @Test
    fun textNormalWithoutHyphen() {
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Normal(None).wrap(input, 20)
        )
        assertEquals(
            listOf("Hello", "world", "!"),
            LineWrap.Normal(None).wrap(input, 5)
        )
        assertEquals(
            listOf("Hell", "o wo", "rld!"),
            LineWrap.Normal(None).wrap(input, 4)
        )
    }

    @Test
    fun textNormalWitHyphen() {
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Normal(Hyphen).wrap(input, 20)
        )
        assertEquals(
            listOf("Hello", "world", "!"),
            LineWrap.Normal(Hyphen).wrap(input, 5)
        )
        // TODO
//        assertEquals(
//            listOf("Hel-", "lo", "wor-", "ld!"),
//            LineWrap.Normal(Hyphen).wrap(input, 4)
//        )
    }

    @Test
    fun testTextTruncated() {
        // truncate start
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Truncate(Alignment.Start).wrap(input, 20)
        )
        assertEquals(
            listOf("... world!"),
            LineWrap.Truncate(Alignment.Start).wrap(input, 10)
        )

        // truncate center
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Truncate(Alignment.Center).wrap(input, 20)
        )
        assertEquals(
            listOf("Hel...rld!"),
            LineWrap.Truncate(Alignment.Center).wrap(input, 10)
        )

        // truncate end
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Truncate(Alignment.End).wrap(input, 20)
        )
        assertEquals(
            listOf("Hello w..."),
            LineWrap.Truncate(Alignment.End).wrap(input, 10)
        )

        // truncate undefined
        assertEquals(
            listOf("Hello world!"),
            LineWrap.Truncate(Alignment.Undefined).wrap(input, 20)
        )
        assertEquals(
            listOf("Hello w..."),
            LineWrap.Truncate(Alignment.Undefined).wrap(input, 10)
        )
    }
}