package kmm.jacky.utilitylibrary.enums

import kmm.jacky.utilitylibrary.extensions.findAllSpaces
import kmm.jacky.utilitylibrary.extensions.firstIndexFrom
import kmm.jacky.utilitylibrary.extensions.process

sealed class LineWrap {

    enum class WordBreakPolicy(internal val text: String) {
        Hyphen("-"),
        None("")
    }

    abstract fun wrap(text: String, boundary: Int): List<String>

    class Normal(private val policy: WordBreakPolicy) : LineWrap() {
        override fun wrap(text: String, boundary: Int): List<String> {
            var pos = 0
            var index = 0
            val result = mutableListOf<String>()
            val spaces = text.findAllSpaces()
            while (pos < text.length) {
                index = spaces.firstIndexFrom(index) { it.range.first >= pos + boundary }
                val processed = text.process(
                    pos, boundary, policy,
                    if (index < 1) pos else spaces[index - 1].range.first,
                    if (index == -1) text.length else spaces[index].range.first
                )
                result.add(processed.first)
                pos = processed.second
            }
            return result
        }
    }

    /**
     * Truncate word when the given string @param text is longer than the constrained boundary.
     *
     * @property alignment: [Alignment.Start], [Alignment.Center], [Alignment.End], [Alignment.Undefined]
     *
     */
    class Truncate(private val alignment: Alignment) : LineWrap() {
        private val hyphenChar: String = "..."

        /**
         * Wrap the text to the given boundary, whole text is returned if its within the constraint.
         *
         * When [Alignment.Undefined] is selected, then it will defaulted to [Alignment.End]
         * ```
         * Input    : Hello world
         * Start    : ... World
         * Center   : Hel...rld
         * End      : Hello ...
         * Undefined: Hello ...
         * ```
         * @param text Input text wrapped if required
         * @param boundary The constrained that total amount of characters is allowed with the given boundary
         * @return Single size string which text is being truncated by its alignment
         */
        override fun wrap(text: String, boundary: Int): List<String> = listOf(
            if (text.length > boundary) {
                when (alignment) {
                    Alignment.Start -> hyphenChar + text.takeLast(boundary - 3)
                    Alignment.Center -> ((boundary - 3) / 2).let {
                        "${text.take(it)}$hyphenChar${text.takeLast(boundary - 3 - it)}"
                    }
                    Alignment.End, Alignment.Undefined -> text.take(boundary - 3) + hyphenChar
                }
            } else {
                text
            }
        )
    }
}
