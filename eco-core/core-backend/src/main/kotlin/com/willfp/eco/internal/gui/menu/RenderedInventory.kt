package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.events.CaptiveItemChangeEvent
import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.MenuUtils
import com.willfp.eco.util.openMenu
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

private val trackedForceRendered = mutableMapOf<UUID, RenderedInventory>()

fun Player.removeForcedRenderedInventory() {
    trackedForceRendered.remove(this.uniqueId)
}

fun Player.forceRenderedInventory(menu: RenderedInventory) {
    trackedForceRendered[this.uniqueId] = menu
}

// Workaround because 1.21 has OpenInventory as an interface instead of an abstract class like in previous versions
interface TopInventoryProxy {
    fun getTopInventory(player: Player): Inventory
}

val Player.renderedInventory: RenderedInventory?
    get() = trackedForceRendered[this.uniqueId]
        ?: this.openInventory.topInventory.asRenderedInventory()

class RenderedInventory(
    val menu: EcoMenu,
    val inventory: Inventory,
    val player: Player
) {
    val captiveItems = mutableMapOf<GUIPosition, ItemStack>()
    val state = mutableMapOf<String, Any?>()

    fun render() {
        // This can happen when opening menus from other menus,
        // fixing a bug where multiple paginated menus on top of
        // each other caused bugs with page changer display.
        if (this.menu != player.openMenu) {
            MenuHandler.unregisterInventory(this.inventory)
            return
        }

        val newCaptive = mutableMapOf<GUIPosition, ItemStack>()

        for (row in (1..menu.rows)) {
            for (column in (1..menu.columns)) {
                val position = GUIPosition(row, column)
                val bukkit = MenuUtils.rowColumnToSlot(row, column, menu.columns)

                val slot = menu.getSlot(row, column, player)
                val renderedItem = slot.getItemStack(player)

                if (slot.isCaptive(player, menu)) {
                    val actualItem = inventory.getItem(bukkit) ?: continue

                    if (slot.isCaptiveFromEmpty) {
                        if (!actualItem.isEcoEmpty) {
                            newCaptive[position] = actualItem
                        }
                    } else {
                        if (actualItem != renderedItem && !EmptyTestableItem().matches(actualItem)) {
                            newCaptive[position] = actualItem
                        }
                    }
                } else {
                    inventory.setItem(bukkit, renderedItem)
                }
            }
        }

        val previousCaptive = captiveItems.toMap()
        captiveItems.clear()
        captiveItems.putAll(newCaptive)

        // Call captive item change event
        for (position in previousCaptive.keys union newCaptive.keys) {
            if (previousCaptive[position] != newCaptive[position]) {
                menu.callEvent(
                    player, CaptiveItemChangeEvent(
                        position.row,
                        position.column,
                        previousCaptive[position],
                        newCaptive[position]
                    )
                )
            }
        }

        menu.runOnRender(player)

        // Run second render if captive items changed
        if (captiveItems != previousCaptive) {
            for (row in (1..menu.rows)) {
                for (column in (1..menu.columns)) {
                    val bukkit = MenuUtils.rowColumnToSlot(row, column, menu.columns)

                    val slot = menu.getSlot(row, column, player)
                    val renderedItem = slot.getItemStack(player)

                    if (!slot.isCaptive(player, menu)) {
                        inventory.setItem(bukkit, renderedItem)
                    }
                }
            }
        }
    }

    fun renderDefaultCaptiveItems() {
        menu.runOnRender(player)

        for (row in (1..menu.rows)) {
            for (column in (1..menu.columns)) {
                val bukkit = MenuUtils.rowColumnToSlot(row, column, menu.columns)

                val slot = menu.getSlot(row, column, player)

                if (slot.isCaptive(player, menu)) {
                    inventory.setItem(bukkit, slot.getItemStack(player))
                }
            }
        }
    }
}
