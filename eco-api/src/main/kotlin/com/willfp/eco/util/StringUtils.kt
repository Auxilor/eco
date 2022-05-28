@file:JvmName("StringUtilsExtensions")

package com.willfp.eco.util

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

/** @see StringUtils.formatList */
fun List<String>.formatEco(
    player: Player? = null,
    formatPlaceholders: Boolean = false
): List<String> = StringUtils.formatList(
    this,
    player,
    if (formatPlaceholders) StringUtils.FormatOption.WITH_PLACEHOLDERS else StringUtils.FormatOption.WITHOUT_PLACEHOLDERS
)

/** @see StringUtils.splitAround */
fun String.splitAround(separator: String): Array<String> =
    StringUtils.splitAround(this, separator)

/** @see StringUtils.toNiceString */
fun Any?.toNiceString(): String =
    StringUtils.toNiceString(this)
