package kmm.jacky.utilitylibrary.enums

import kmm.jacky.utilitylibrary.extensions.findAllPunctuations
import kmm.jacky.utilitylibrary.extensions.findAllSpaces
import kmm.jacky.utilitylibrary.extensions.firstIndexFrom

sealed class LineWrap {

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

        enum class WordBreakPolicy(internal val text: String) {
            Hyphen("-"),
            None("")
        }

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
                        -1 -> spaces.lastOrNull()?.range?.last?.let { it + 1 }
                            ?: pos // + 1 so it's after the space
                        else -> spaces[index - 1].range.last + 1 // + 1 so its after the space
                    },
                    if (index == -1) text.length else spaces[index].range.first
                )
                result.add(processed.first)
                pos = processed.second
            }
            return result
        }

        private fun String.process(
            pos: Int,
            boundary: Int,
            wordBreakPolicy: WordBreakPolicy,
            pointer0: Int,
            pointer1: Int
        ): Pair<String, Int> {
            val wordWontFit = if (wordBreakPolicy == WordBreakPolicy.Hyphen)
                substring(pointer0 until pointer1)
            else ""

            val shouldBreakWord = wordBreakPolicy == WordBreakPolicy.Hyphen &&
                    wordWontFit.length > boundary

            val punctuation = if (shouldBreakWord)
                wordWontFit.findAllPunctuations().lastOrNull { it.range.first <= boundary }
            else null

            val shouldTruncate = shouldBreakWord &&
                    (boundary - pointer0 + pos) > 2 && // can fit at least 3 characters
                    pos <= pointer0

            val shouldAddTruncatedHyphen = shouldTruncate && punctuation == null

            val end = when {
                shouldTruncate && punctuation != null ->
                    pos + punctuation.range.first // break at any punctuation
                shouldTruncate -> pos + boundary - wordBreakPolicy.text.length
                (pointer1 - pos) < boundary -> pointer1
                pos < pointer0 -> pointer0
                else -> pos + boundary
            }

            val result =
                substring(pos until end) + (if (shouldAddTruncatedHyphen) wordBreakPolicy.text else "")

            val offset = when {
                shouldAddTruncatedHyphen -> -1 // -1 if its truncated with hyphen
                end < length && this[end] == ' ' -> 1 // add offset 1 if its a space
                else -> 0
            }
            return Pair(result, pos + result.length + offset)
        }
    }

    /**
     * Truncate word when the given string @param text is longer than the constrained boundary.
     * This required less computation as it only render single line
     *
     * @property policy: [Policy.Start], [Policy.Center], [Policy.End]
     *
     */
    class Truncate(private val policy: Truncate.Policy) : LineWrap() {

        enum class Policy {
            Start,
            Center,
            End
        }

        private val ellipsis = "..."

        /**
         * Wrap the text to the given boundary, whole text is returned if its within the constraint.
         *
         * ```
         * Input    : Hello world
         * Start    : ... World
         * Center   : Hel...rld
         * End      : Hello ...
         * ```
         * @param text Input text wrapped if required
         * @param boundary The constrained that total amount of characters is allowed with the given boundary
         * @return Single size string which text is being truncated by its alignment
         */
        override fun wrap(text: String, boundary: Int): List<String> = listOf(
            if (text.length > boundary) {
                when (policy) {
                    Policy.Start -> ellipsis + text.takeLast(boundary - 3)
                    Policy.Center -> ((boundary - 3) / 2).let {
                        "${text.take(it)}$ellipsis${text.takeLast(boundary - 3 - it)}"
                    }
                    Policy.End -> text.take(boundary - 3) + ellipsis
                }
            } else {
                text
            }
        )
    }
}
