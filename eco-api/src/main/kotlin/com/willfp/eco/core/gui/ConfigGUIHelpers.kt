@file:JvmName("ConfigGUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.page.PageBuilder
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems

/**
 * Get a filler mask from a config section.
 *
 * Reads `path.mask.items`, falling back to the older `path.mask.materials`, and `path.mask.pattern`.
 *
 * @param path The path of the section holding `mask`, or empty for this config itself.
 * @return The filler mask.
 */
fun Config.getFillerMask(path: String = ""): FillerMask {
    val prefix = if (path.isEmpty()) "" else "$path."

    return FillerMask(
        MaskItems.fromItemNames(getStrings("${prefix}mask.items").ifEmpty { getStrings("${prefix}mask.materials") }),
        *getStrings("${prefix}mask.pattern").toTypedArray()
    )
}

/**
 * Set the background mask from a config section.
 *
 * @see getFillerMask
 */
fun MenuBuilder.setMask(config: Config, path: String = ""): MenuBuilder =
    this.setMask(config.getFillerMask(path))

/**
 * Set the background mask from a config section.
 *
 * @see getFillerMask
 */
fun PageBuilder.setMask(config: Config, path: String = ""): PageBuilder =
    this.setMask(config.getFillerMask(path))
