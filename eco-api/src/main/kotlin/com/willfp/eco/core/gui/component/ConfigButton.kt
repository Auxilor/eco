package com.willfp.eco.core.gui.component

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.gui.SlotLocation
import com.willfp.eco.core.gui.getSlotLocations
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.onLeftClick
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

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
 * @param action Runs when a player left clicks the button.
 */
class ConfigButton(
    config: Config,
    defaultItem: String? = null,
    name: String? = null,
    enabledByDefault: Boolean = true,
    defaultLocations: List<SlotLocation>? = null,
    override val layer: Int = MenuLayer.MIDDLE,
    action: (Player, InventoryClickEvent, Slot, Menu) -> Unit = { _, _, _, _ -> }
) : PositionedComponent {
    private val slot = slot(
        ItemStackBuilder(
            Items.lookup(config.getStringOrNull("item") ?: config.getStringOrNull("material") ?: defaultItem ?: "")
        )
            .apply {
                (name ?: config.getStringOrNull("name"))?.let { setDisplayName(it) }

                if (config.has("lore")) {
                    addLoreLines(config.getStrings("lore"))
                }
            }
            .build()
    ) {
        onLeftClick(action)
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
    layer: Int = MenuLayer.MIDDLE
): ConfigButton = ConfigButton(config, defaultItem, name, enabledByDefault, defaultLocations, layer) { player, _, _, _ ->
    player.closeInventory()
}
