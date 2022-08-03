package kmm.jacky.utilitylibrary

import kmm.jacky.utilitylibrary.enums.Alignment
import kmm.jacky.utilitylibrary.enums.LineWrap
import kmm.jacky.utilitylibrary.enums.LineWrap.WordBreakPolicy.Hyphen
import kmm.jacky.utilitylibrary.enums.LineWrap.WordBreakPolicy.None
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
            listOf("Hell", "o ", "worl", "d!"),
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
        assertEquals(
            listOf("Hel-", "lo ", "wor-", "ld!"),
            LineWrap.Normal(Hyphen).wrap(input, 4)
        )
    }

    @Test
    fun testNormalWithLongerText() {
        val longText =
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin ullamcorper suscipit neque. Vivamus augue est, hendrerit a gravida at, pharetra eget mauris. Mauris sollicitudin mauris vel purus maximus sodales. Donec lobortis diam orci, non luctus nisl suscipit a. Praesent convallis diam quis vehicula faucibus. Integer ac porta sapien. Sed sit."

        val expect = listOf(
            "Lorem ipsum dolor sit amet, consectetur ",
            "adipiscing elit. Proin ullamcorper ",
            "suscipit neque. Vivamus augue est, ",
            "hendrerit a gravida at, pharetra eget ",
            "mauris. Mauris sollicitudin mauris vel ",
            "purus maximus sodales. Donec lobortis ",
            "diam orci, non luctus nisl suscipit a. ",
            "Praesent convallis diam quis vehicula ",
            "faucibus. Integer ac porta sapien. Sed ",
            "sit."
        )
        val actual = LineWrap.Normal(Hyphen).wrap(longText, 40)
        assertEquals(expect, actual)
        expect.forEach { assertTrue(it.length <= 40) }
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