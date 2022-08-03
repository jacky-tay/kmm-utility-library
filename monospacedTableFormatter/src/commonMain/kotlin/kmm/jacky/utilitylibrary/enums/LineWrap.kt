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

    /**
     * Wrap the text when the given string @param text is longer than the constrained boundary.
     * There are two types of word break policy: [WordBreakPolicy.Hyphen] and [WordBreakPolicy.None]
     * Word break policy will only be applied if the whole word can not be fitted in single line,
     * ie the boundary is smaller than the word's length.
     *
     * [WordBreakPolicy.Hyphen] will add '-' if the word cannot be fitted in line,
     * the remaining word will be wrapped in the next line.
     * [WordBreakPolicy.None] will break the word if the word cannot be fitted in line,
     * the remaining word will be wrapped in the next line.
     *
     * @property policy The word breaking policy
     * @constructor Create empty Normal
     */
    class Normal(private val policy: WordBreakPolicy) : LineWrap() {
        override fun wrap(text: String, boundary: Int): List<String> {
            var pos = 0
            var index = 0
            val result = mutableListOf<String>()
            val spaces = text.findAllSpaces()
            while (pos < text.length) {
                index = spaces.firstIndexFrom(index) { it.range.first >= pos + boundary }
                val processed = text.process(
                    pos, boundary, policy, when (index) {
                        0 -> pos
                        -1 -> spaces.lastOrNull()?.range?.last?.let { it + 1 } ?: pos // + 1 so it's after the space
                        else -> spaces[index - 1].range.last + 1 // + 1 so its after the space
                    },
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
     * This required less computation as it only render single line
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
