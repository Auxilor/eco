package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.events.CaptiveItemChangeEvent
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.openMenu
import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

private val emptyTestableItem = EmptyTestableItem()

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
        if (this.menu != player.openMenu) {
            MenuHandler.unregisterInventory(this.inventory)
            return
        }

        val rows = menu.rows
        val columns = menu.columns
        val totalSlots = rows * columns
        val newCaptive = mutableMapOf<GUIPosition, ItemStack>()

        val slotCache = arrayOfNulls<Slot>(totalSlots)
        val captiveFlags = BooleanArray(totalSlots)

        var bukkit = 0
        for (row in (1..rows)) {
            for (column in (1..columns)) {
                val slot = menu.getSlot(row, column, player)
                slotCache[bukkit] = slot

                if (slot.isCaptive(player, menu)) {
                    captiveFlags[bukkit] = true
                    val actualItem = inventory.getItem(bukkit)

                    if (actualItem != null) {
                        if (slot.isCaptiveFromEmpty) {
                            if (!actualItem.isEcoEmpty) {
                                newCaptive[GUIPosition(row, column)] = actualItem
                            }
                        } else {
                            if (actualItem != slot.getItemStack(player)
                                && !emptyTestableItem.matches(actualItem)
                            ) {
                                newCaptive[GUIPosition(row, column)] = actualItem
                            }
                        }
                    }
                } else {
                    inventory.setItem(bukkit, slot.getItemStack(player))
                }

                bukkit++
            }
        }

        val previousCaptive = captiveItems.toMap()
        captiveItems.clear()
        captiveItems.putAll(newCaptive)

        var captiveChanged = false
        for (position in previousCaptive.keys union newCaptive.keys) {
            if (previousCaptive[position] != newCaptive[position]) {
                captiveChanged = true
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

        if (captiveChanged) {
            for (i in 0 until totalSlots) {
                if (!captiveFlags[i]) {
                    inventory.setItem(i, slotCache[i]!!.getItemStack(player))
                }
            }
        }
    }

    fun renderDefaultCaptiveItems() {
        menu.runOnRender(player)

        var bukkit = 0
        for (row in (1..menu.rows)) {
            for (column in (1..menu.columns)) {
                val slot = menu.getSlot(row, column, player)

                if (slot.isCaptive(player, menu)) {
                    inventory.setItem(bukkit, slot.getItemStack(player))
                }

                bukkit++
            }
        }
    }
}
