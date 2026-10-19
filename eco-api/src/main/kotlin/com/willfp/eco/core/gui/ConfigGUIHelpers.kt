@file:JvmName("ConfigGUIHelperExtensions")

package com.willfp.eco.core.gui

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.menu.MenuBuilder
import com.willfp.eco.core.gui.page.PageBuilder
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import org.bukkit.inventory.ItemStack

internal fun Config.getPlainStringOrNull(path: String): String? =
    if (getSubsectionOrNull(path) == null) getStringOrNull(path) else null

/**
 * Get a GUI item from a config section.
 *
 * The item lookup string is `item.<state>` when [state] is set, then `item`, then `material`,
 * then [defaultItem]. The name is [name], else `name.<state>`, else `name`. The lore is [lore],
 * else `lore.<state>`, else `lore`. The looked up item is used as it is when no name and no lore
 * apply, and built with [ItemStackBuilder] otherwise.
 *
 * @param defaultItem The item lookup string when the config sets none, or null.
 * @param name The display name, used instead of the config's when set.
 * @param lore The lore, used instead of the config's when set.
 * @param state The key read first under `item`, `name` and `lore`, or null.
 * @param invalidItem The item lookup string used when the configured item does not resolve, or null.
 * @param format Applied to the item lookup string and to every name and lore line read from config.
 * @return The item.
 */
fun Config.getConfigItem(
    defaultItem: String? = null,
    name: String? = null,
    lore: List<String>? = null,
    state: String? = null,
    invalidItem: String? = null,
    format: (String) -> String = { it }
): ItemStack {
    val lookup = (state?.let { getPlainStringOrNull("item.$it") }
        ?: getPlainStringOrNull("item")
        ?: getStringOrNull("material")
        ?: defaultItem
        ?: "").let(format)

    val base = Items.lookup(lookup).let {
        if (it is EmptyTestableItem && invalidItem != null) Items.lookup(invalidItem) else it
    }.item

    val displayName = name ?: (state?.let { getPlainStringOrNull("name.$it") } ?: getPlainStringOrNull("name"))?.let(format)

    val lorePath = state?.let { "lore.$it" }?.takeIf { has(it) } ?: "lore".takeIf { has(it) }

    val loreLines = lore ?: lorePath?.let { getStrings(it) }?.map(format)

    if (displayName == null && loreLines == null) {
        return base
    }

    return ItemStackBuilder(base)
        .apply {
            displayName?.let { setDisplayName(it) }
            loreLines?.let { addLoreLines(it) }
        }
        .build()
}

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
