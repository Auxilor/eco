@file:JvmName("StringUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/**
 * Parse this legacy (bukkit) string into a component.
 *
 * Reads the section sign (`§`) as the colour character, including the `§x§R§R§G§G§B§B`
 * repeated-character form used for hex colours. Results are cached.
 *
 * @return The component.
 * @see StringUtils.toComponent
 */
fun String.toComponent(): Component =
    StringUtils.toComponent(this)

/**
 * Parse this JSON string into a component.
 *
 * @return The component, or an empty component if the string is empty or is not valid
 * component JSON.
 * @see StringUtils.jsonToComponent
 */
fun String.jsonToComponent(): Component =
    StringUtils.jsonToComponent(this)

/**
 * Serialize this component to a legacy (bukkit) string.
 *
 * Uses the section sign (`§`) as the colour character, writing hex colours in the
 * `§x§R§R§G§G§B§B` repeated-character form. Results are cached.
 *
 * @return The legacy string.
 * @see StringUtils.toLegacy
 */
fun Component.toLegacy(): String =
    StringUtils.toLegacy(this)

/**
 * Serialize this component to JSON.
 *
 * The component is wrapped in an empty parent with italics explicitly disabled, so that item
 * names and lore built from it are not rendered in italics by default.
 *
 * @return The JSON string, or the JSON for an empty component if serialization fails.
 * @see StringUtils.componentToJson
 */
fun Component.toJSON(): String =
    StringUtils.componentToJson(this)

/**
 * Format this string for display, translating colour codes and, optionally, placeholders.
 *
 * When [formatPlaceholders] is true, `%placeholder%` placeholders (the PlaceholderAPI format)
 * are resolved against [player] first. Note that this defaults to false, so by default no
 * placeholders are translated and [player] has no effect.
 *
 * Colour codes are then processed in this order:
 * 1. MiniMessage tags, such as `<red>` and `<bold>`.
 * 2. Ampersand legacy codes: `&` followed by any of `0`-`9`, `a`-`f`, `k`-`o`, `r` or `x`
 *    (case insensitive), converted to the equivalent `§` code.
 * 3. Gradients, in any of the forms `<GRADIENT:RRGGBB>text</GRADIENT:RRGGBB>`,
 *    `<GRADIENT:#RRGGBB>…</GRADIENT:#RRGGBB>`, `<G:RRGGBB>…</G:RRGGBB>`,
 *    `<G:#RRGGBB>…</G:#RRGGBB>`, `<G#RRGGBB>…</G#RRGGBB>`, `<#:RRGGBB>…</#:RRGGBB>`,
 *    `{#:RRGGBB}…{/#:RRGGBB}` and `{#RRGGBB>}…{#RRGGBB<}`.
 * 4. Hex colours, in any of the forms `&#RRGGBB`, `{#RRGGBB}` and `<#RRGGBB>`.
 *
 * The result is a legacy string using `§`, with hex colours in the `§x§R§R§G§G§B§B` form.
 *
 * @param player             The player to translate placeholders with respect to.
 * @param formatPlaceholders If placeholders should be translated as well as colour codes.
 * @return The formatted string.
 * @see StringUtils.format
 */
fun String.formatEco(
    player: Player? = null,
    formatPlaceholders: Boolean = false
) = StringUtils.format(
    this,
    player,
    if (formatPlaceholders) StringUtils.FormatOption.WITH_PLACEHOLDERS else StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
)

/**
 * Format this string for display, translating `%placeholder%` placeholders against a context
 * and then translating colour codes.
 *
 * Placeholders are always translated by this overload. The colour code formats processed are
 * exactly those listed on [String.formatEco].
 *
 * @param context The context to translate placeholders with respect to.
 * @return The formatted string.
 * @see StringUtils.format
 */
fun String.formatEco(
    context: PlaceholderContext
) = StringUtils.format(
    this,
    context
)

/**
 * Format each string in this list for display, translating colour codes and, optionally,
 * placeholders.
 *
 * Each element is formatted individually, exactly as by [String.formatEco], which lists the
 * placeholder and colour code formats processed. Note that [formatPlaceholders] defaults to
 * false, so by default no placeholders are translated and [player] has no effect.
 *
 * @param player             The player to translate placeholders with respect to.
 * @param formatPlaceholders If placeholders should be translated as well as colour codes.
 * @return A new list of formatted strings, in the same order.
 * @see StringUtils.formatList
 */
fun List<String>.formatEco(
    player: Player? = null,
    formatPlaceholders: Boolean = false
): List<String> = StringUtils.formatList(
    this,
    player,
    if (formatPlaceholders) StringUtils.FormatOption.WITH_PLACEHOLDERS else StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
)

/**
 * Format each string in this list for display, translating `%placeholder%` placeholders
 * against a context and then translating colour codes.
 *
 * Placeholders are always translated by this overload. Each element is formatted individually,
 * exactly as by [String.formatEco], which lists the colour code formats processed.
 *
 * @param context The context to translate placeholders with respect to.
 * @return A new list of formatted strings, in the same order.
 * @see StringUtils.formatList
 */
fun List<String>.formatEco(
    context: PlaceholderContext
): List<String> = StringUtils.formatList(
    this,
    context
)

/**
 * Split this string around a separator, but only where the separator is surrounded by spaces.
 *
 * The separator is matched literally, not as a regex. For example, splitting
 * `"hello ? how are you"` around `"?"` splits, but splitting `"hello? how are you"` does not.
 * The spaces around the separator are consumed along with it.
 *
 * @param separator The separator to split around.
 * @return The parts of the string, as an array.
 * @see StringUtils.splitAround
 */
fun String.splitAround(separator: String): Array<String> =
    StringUtils.splitAround(this, separator)

/**
 * Convert this object to a display string, formatting numbers and collections more nicely
 * than `toString` would.
 *
 * Null becomes `"null"`, doubles are rendered to two decimal places and drop the decimals
 * entirely when they are zero, and collections are rendered by converting each element the
 * same way and joining them with `", "`. Anything else falls back to its string value.
 *
 * @return The string representation.
 * @see StringUtils.toNiceString
 */
fun Any?.toNiceString(): String =
    StringUtils.toNiceString(this)

/**
 * Replace every occurrence of a literal substring in this string.
 *
 * Behaves like `String.replace`, but is faster because it pre-sizes its buffer rather than
 * going via a regex.
 *
 * @param target      The substring to replace.
 * @param replacement The string to replace it with.
 * @return The resulting string, or the receiver itself if the target does not occur.
 * @see StringUtils.replaceQuickly
 */
fun String.replaceQuickly(target: String, replacement: String): String =
    StringUtils.replaceQuickly(this, target, replacement)

/**
 * Wrap this string into lines of roughly a given width, preserving colours and decorations
 * across the line break.
 *
 * The string is broken at the first whitespace character after a line has passed [width]
 * characters, so individual lines may run slightly over it. The width is counted in characters
 * of the parsed component, not in bytes or pixels.
 *
 * @param width          The target length of each line, in characters.
 * @param preserveMargin If the receiver's leading whitespace margin should be re-applied to the
 *                       start of every line after the first.
 * @return The wrapped lines, as legacy strings.
 * @see StringUtils.lineWrap
 */
fun String.lineWrap(width: Int, preserveMargin: Boolean = true): List<String> =
    StringUtils.lineWrap(this, width, preserveMargin)

/**
 * Wrap each string in this list into lines of roughly a given width, preserving colours and
 * decorations across the line break.
 *
 * Each element is wrapped individually, exactly as by [String.lineWrap], and the resulting
 * lines are flattened into a single list in order.
 *
 * @param width          The target length of each line, in characters.
 * @param preserveMargin If each element's leading whitespace margin should be re-applied to the
 *                       start of every line after its first.
 * @return The wrapped lines, as legacy strings.
 * @see StringUtils.lineWrap
 */
fun List<String>.lineWrap(width: Int, preserveMargin: Boolean = true): List<String> =
    StringUtils.lineWrap(this, width, preserveMargin)

/**
 * Convert this string to title case, by capitalising the first character of each
 * space-separated word and lowercasing the rest of it.
 *
 * Splits on single spaces only, and preserves the original spacing.
 *
 * @return The title-cased string.
 * @see StringUtils.toTitleCase
 */
fun String.titlecase(): String =
    StringUtils.toTitleCase(this)

/**
 * The margin of this string: the number of leading whitespace characters before its trimmed
 * content begins.
 *
 * @see StringUtils.getMargin
 */
val String.margin: Int
    get() = StringUtils.getMargin(this)
