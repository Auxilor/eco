@file:JvmName("PlaceholderExtensions")

package com.willfp.eco.core.placeholder

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.context.PlaceholderContext

/** @see PlaceholderManager.translatePlaceholders */
fun String.translatePlaceholders(context: PlaceholderContext) =
    PlaceholderManager.translatePlaceholders(this, context)

/** @see PlaceholderManager.findPlaceholdersIn */
fun String.findPlaceholders(): List<String> =
    PlaceholderManager.findPlaceholdersIn(this)
