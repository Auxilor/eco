@file:JvmName("StringUtilsExtensions")

package com.willfp.eco.util

import com.willfp.eco.core.placeholder.context.PlaceholderContext
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

/** @see StringUtils.toComponent */
fun String.toComponent(): Component =
    StringUtils.toComponent(this)

/** @see StringUtils.jsonToComponent */
fun String.jsonToComponent(): Component =
    StringUtils.jsonToComponent(this)

/** @see StringUtils.toLegacy */
fun Component.toLegacy(): String =
    StringUtils.toLegacy(this)

/** @see StringUtils.componentToJson */
fun Component.toJSON(): String =
    StringUtils.componentToJson(this)

/** @see StringUtils.format */
fun String.formatEco(
    player: Player? = null,
    formatPlaceholders: Boolean = false
) = StringUtils.format(
    this,
    player,
    if (formatPlaceholders) StringUtils.FormatOption.WITH_PLACEHOLDERS else StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
)

/** @see StringUtils.format */
fun String.formatEco(
    context: PlaceholderContext
) = StringUtils.format(
    this,
    context
)

/** @see StringUtils.formatList */
fun List<String>.formatEco(
    player: Player? = null,
    formatPlaceholders: Boolean = false
): List<String> = StringUtils.formatList(
    this,
    player,
    if (formatPlaceholders) StringUtils.FormatOption.WITH_PLACEHOLDERS else StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
)

/** @see StringUtils.formatList */
fun List<String>.formatEco(
    context: PlaceholderContext
): List<String> = StringUtils.formatList(
    this,
    context
)

/** @see StringUtils.splitAround */
fun String.splitAround(separator: String): Array<String> =
    StringUtils.splitAround(this, separator)

/** @see StringUtils.toNiceString */
fun Any?.toNiceString(): String =
    StringUtils.toNiceString(this)

/** @see StringUtils.replaceQuickly */
fun String.replaceQuickly(target: String, replacement: String): String =
    StringUtils.replaceQuickly(this, target, replacement)

/** @see StringUtils.lineWrap */
fun String.lineWrap(width: Int, preserveMargin: Boolean = true): List<String> =
    StringUtils.lineWrap(this, width, preserveMargin)

/** @see StringUtils.lineWrap */
fun List<String>.lineWrap(width: Int, preserveMargin: Boolean = true): List<String> =
    StringUtils.lineWrap(this, width, preserveMargin)

/** @see StringUtils.getMargin */
val String.margin: Int
    get() = StringUtils.getMargin(this)
