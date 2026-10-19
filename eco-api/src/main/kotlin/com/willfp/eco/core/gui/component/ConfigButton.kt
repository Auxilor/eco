package com.willfp.eco.core.gui.component

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.getConfigItem
import com.willfp.eco.core.gui.getSlotLocations
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.sound.AbstractPlayableSound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * A button read from a config section.
 *
 * Reads `item` (or `material`), `name`, `lore`, `enabled` and the keys of
 * [getSlotLocations] from [config].
 *
 * @param config The config section of the button.
 * @param defaultItem The item lookup string when the config sets neither `item` nor `material`.
 * @param name The display name, used instead of the config's `name` when set.
 * @param enabledByDefault Whether the button is enabled when the config does not set `enabled`.
 * @param defaultLocations The locations when the config sets none.
 * @param layer The layer to place the button on.
 * @param lore The lore, used instead of the config's `lore` when set.
 * @param state The key read first under `item`, `name` and `lore` (for example `active`), or null.
 * @param invalidItem The item lookup string used when the configured item does not resolve, or null.
 * @param format Applied to the item lookup string and to every name and lore line read from config.
 * @param itemModifier Applied to the built item, or null.
 * @param sound Played to the player before [action], or null.
 * @param action Runs when a player left clicks the button.
 */
class ConfigButton(
    config: Config,
    defaultItem: String? = null,
    name: String? = null,
    enabledByDefault: Boolean = true,
    defaultLocations: List<SlotLocation>? = null,
    override val layer: Int = MenuLayer.MIDDLE,
    lore: List<String>? = null,
    state: String? = null,
    invalidItem: String? = null,
    format: (String) -> String = { it },
    itemModifier: ((ItemStack) -> ItemStack)? = null,
    sound: AbstractPlayableSound<*>? = null,
    action: (Player, InventoryClickEvent, Slot, Menu) -> Unit = { _, _, _, _ -> }
) : PositionedComponent {
    private val slot = slot(
        config.getConfigItem(defaultItem, name, lore, state, invalidItem, format).let { itemModifier?.invoke(it) ?: it }
    ) {
        onLeftClick { player, event, slot, menu ->
            sound?.playTo(player)
            action(player, event, slot, menu)
        }
    }

    override val isEnabled = config.getBoolOrNull("enabled") ?: enabledByDefault

    override val locations = config.getSlotLocations(default = defaultLocations)

    override fun getSlotAt(row: Int, column: Int) = slot
}

/** A [ConfigButton] that closes the menu when left clicked. */
fun closeButton(
    config: Config,
    defaultItem: String? = null,
    name: String? = null,
    enabledByDefault: Boolean = true,
    defaultLocations: List<SlotLocation>? = null,
    layer: Int = MenuLayer.MIDDLE,
    lore: List<String>? = null,
    state: String? = null,
    invalidItem: String? = null,
    format: (String) -> String = { it },
    itemModifier: ((ItemStack) -> ItemStack)? = null,
    sound: AbstractPlayableSound<*>? = null
): ConfigButton = ConfigButton(
    config,
    defaultItem,
    name,
    enabledByDefault,
    defaultLocations,
    layer,
    lore,
    state,
    invalidItem,
    format,
    itemModifier,
    sound
) { player, _, _, _ ->
    player.closeInventory()
}
