package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.items.isEmpty
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.MenuUtils
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
        captiveItems.clear()

        for (row in (1..menu.rows)) {
            for (column in (1..menu.columns)) {
                val position = GUIPosition(row, column)
                val bukkit = MenuUtils.rowColumnToSlot(row, column, menu.columns)

                val slot = menu.getSlot(row, column, player, menu)
                val renderedItem = slot.getItemStack(player)

                if (slot.isCaptive(player, menu)) {
                    val actualItem = inventory.getItem(bukkit) ?: continue

                    if (slot.isCaptiveFromEmpty) {
                        if (!actualItem.isEmpty) {
                            captiveItems[position] = actualItem
                        }
                    } else {
                        if (actualItem != renderedItem && !EmptyTestableItem().matches(actualItem)) {
                            captiveItems[position] = actualItem
                        }
                    }
                } else {
                    inventory.setItem(bukkit, renderedItem)
                }
            }
        }

        menu.runOnRender(player)
    }

    fun renderDefaultCaptiveItems() {
        for (row in (1..menu.rows)) {
            for (column in (1..menu.columns)) {
                val bukkit = MenuUtils.rowColumnToSlot(row, column, menu.columns)

                val slot = menu.getSlot(row, column, player, menu)

                if (slot.isCaptive(player, menu)) {
                    inventory.setItem(bukkit, slot.getItemStack(player))
                }
            }
        }
    }
}
