@file:JvmName("ConfigGUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.page.PageBuilder
import com.willfp.eco.core.gui.slot.ConfigSlot
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

/**
 * Add a custom slot from a config section.
 *
 * One [ConfigSlot] is built from [slotConfig] and placed at every location from [getSlotLocations].
 *
 * @param slotConfig The config of the slot.
 * @return The builder.
 */
fun MenuBuilder.addCustomSlot(slotConfig: Config): MenuBuilder {
    val slot = ConfigSlot(slotConfig)

    for ((row, column) in slotConfig.getSlotLocations()) {
        addComponent(row, column, slot)
    }

    return this
}

/**
 * Add a custom slot from a config section.
 *
 * One [ConfigSlot] is built from [slotConfig] and placed at every location from [getSlotLocations].
 *
 * @param slotConfig The config of the slot.
 * @return The builder.
 */
fun PageBuilder.addCustomSlot(slotConfig: Config): PageBuilder {
    val slot = ConfigSlot(slotConfig)

    for ((row, column) in slotConfig.getSlotLocations()) {
        addComponent(row, column, slot)
    }

    return this
}

/**
 * Add every custom slot listed under `path.custom-slots`.
 *
 * @param config The config holding the section.
 * @param path The path of the section holding `custom-slots`, or empty for the config itself.
 * @return The builder.
 * @see addCustomSlot
 */
fun MenuBuilder.addCustomSlots(config: Config, path: String): MenuBuilder {
    val prefix = if (path.isEmpty()) "" else "$path."

    for (slotConfig in config.getSubsections("${prefix}custom-slots")) {
        addCustomSlot(slotConfig)
    }

    return this
}

/**
 * Add every custom slot listed under `path.custom-slots`.
 *
 * @param config The config holding the section.
 * @param path The path of the section holding `custom-slots`, or empty for the config itself.
 * @return The builder.
 * @see addCustomSlot
 */
fun PageBuilder.addCustomSlots(config: Config, path: String): PageBuilder {
    val prefix = if (path.isEmpty()) "" else "$path."

    for (slotConfig in config.getSubsections("${prefix}custom-slots")) {
        addCustomSlot(slotConfig)
    }

    return this
}
