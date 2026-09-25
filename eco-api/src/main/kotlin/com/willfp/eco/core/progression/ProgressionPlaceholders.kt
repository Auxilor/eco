package com.willfp.eco.core.progression

import com.willfp.eco.util.toNumeral
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * The placeholders that describe a point in a progression, and the offsets around it.
 *
 * Every levelling plugin grew its own copy of the same block - four literal duplicates of
 * `Regex("%level_(-?\\d+)(_numeral)?%")`, plus a hand-written `.replace` for each of the four
 * fixed names, each subtly free to drift from the others. This is that logic, once, so a
 * `%level_2%` means the same thing in a skill's lore, a job's message, a minion's hologram and
 * an effect chain.
 *
 * [type] is the progression's own word for a step - `level`, `tier`, `rank`. It is never
 * defaulted, because a config written in tiers should not start needing `%level%`.
 */
object ProgressionPlaceholders {
    /**
     * The pattern for the offset placeholders, **without** `%` delimiters.
     *
     * Delimiter-free because eco matches injected placeholders against the text *between* the
     * delimiters; [inject] adds them back for plain string replacement.
     */
    @JvmStatic
    fun offsetPattern(type: String): Pattern =
        Pattern.compile("${Pattern.quote(type)}_([+-]?\\d+)(_numeral)?")

    /**
     * Resolve a single offset placeholder, given the text between the `%` delimiters.
     *
     * @param args  The placeholder body, e.g. `level_2` or `level_-1_numeral`.
     * @param type  The progression's word for a step.
     * @param value The current value.
     * @return The resolved value, or null if [args] is not an offset placeholder for [type].
     */
    @JvmStatic
    fun resolveOffset(args: String, type: String, value: Int): String? {
        val matcher = offsetPattern(type).matcher(args)

        if (!matcher.matches()) {
            return null
        }

        val offset = matcher.group(1)?.toIntOrNull() ?: return null

        return format(value + offset, numeral = matcher.group(2) != null)
    }

    /**
     * Replace every progression placeholder in [text].
     *
     * Handles `%<type>%`, `%<type>_numeral%`, `%previous_<type>%`,
     * `%previous_<type>_numeral%`, and `%<type>_N%` / `%<type>_N_numeral%` for any integer N.
     *
     * @param text  The text to inject into.
     * @param type  The progression's word for a step.
     * @param value The current value.
     * @return The text, with the placeholders replaced.
     */
    @JvmStatic
    fun inject(text: String, type: String, value: Int): String {
        // The fixed names first. The offset pattern requires digits after the underscore, so
        // it cannot swallow `%level_numeral%` - but doing these first keeps that independent
        // of the pattern rather than relying on it.
        var result = text
            .replace("%${type}_numeral%", format(value, numeral = true))
            .replace("%previous_${type}_numeral%", format(value - 1, numeral = true))
            .replace("%previous_$type%", (value - 1).toString())
            .replace("%$type%", value.toString())

        val buffer = StringBuilder()
        val delimited = Pattern
            .compile("%(${Pattern.quote(type)}_[+-]?\\d+(?:_numeral)?)%")
            .matcher(result)

        while (delimited.find()) {
            val resolved = resolveOffset(delimited.group(1), type, value) ?: delimited.group()

            // quoteReplacement: a resolved value is data, and a stray $ or \ in it must not be
            // read as a group reference.
            delimited.appendReplacement(buffer, Matcher.quoteReplacement(resolved))
        }

        delimited.appendTail(buffer)

        return buffer.toString()
    }

    /**
     * Roman numerals cannot express zero or a negative, and an offset can walk below both, so
     * those fall back to the plain number rather than letting a conversion throw from inside
     * a tick.
     */
    private fun format(value: Int, numeral: Boolean): String =
        if (numeral && value > 0) value.toNumeral() else value.toString()
}
