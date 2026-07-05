package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.events.CaptiveItemChangeEvent
import com.willfp.eco.core.items.isEcoEmpty
import com.willfp.eco.core.recipe.parts.EmptyTestableItem
import com.willfp.eco.util.openMenu
import java.util.UUID
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

private val emptyTestableItem = EmptyTestableItem()

private const val RENDERED_TITLE_KEY = "eco:rendered_title"

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

        val captiveFlags = BooleanArray(totalSlots)

        var bukkit = 0
        for (row in (1..rows)) {
            for (column in (1..columns)) {
                val slot = menu.getSlot(row, column, player)

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

        refreshDynamicTitle()

        if (captiveChanged) {
            var i = 0
            for (row in (1..rows)) {
                for (column in (1..columns)) {
                    if (!captiveFlags[i]) {
                        inventory.setItem(i, menu.getSlot(row, column, player).getItemStack(player))
                    }
                    i++
                }
            }
        }
    }

    fun refreshDynamicTitle() {
        val template = menu.getTitle()

        if (!template.contains("%page%") && !template.contains("%max_page%")) {
            return
        }

        val resolved = template
            .replace("%page%", menu.getPage(player).toString())
            .replace("%max_page%", menu.getMaxPage(player).toString())

        if (state[RENDERED_TITLE_KEY] == resolved) {
            return
        }

        // Skip the first render inside open(), before this inventory becomes the
        // player's open top inventory. setTitle would target the wrong view.
        if (player.openInventory.topInventory !== inventory) {
            return
        }

        @Suppress("DEPRECATION")
        player.openInventory.setTitle(resolved)
        state[RENDERED_TITLE_KEY] = resolved
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
