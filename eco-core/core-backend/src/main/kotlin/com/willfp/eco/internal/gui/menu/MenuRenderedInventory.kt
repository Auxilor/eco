package com.willfp.eco.internal.gui.menu

import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.util.MenuUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.UUID

private val trackedForceMenus = mutableMapOf<UUID, Menu>()

fun Player.forceMenuOpen(menu: Menu) {
    trackedForceMenus[this.uniqueId] = menu
}

fun Player.stopForceMenuOpen() {
    trackedForceMenus.remove(this.uniqueId)
}

val Player.openMenuInternal: Menu?
    get() = trackedForceMenus[this.uniqueId]
        ?: this.openInventory.topInventory.getMenu()

class MenuRenderedInventory(
    val menu: EcoMenu,
    val inventory: Inventory,
    val player: Player
) {
    val captiveItems = mutableListOf<ItemStack>()
    val state = mutableMapOf<String, Any?>()

    fun render() {
        for (row in (1..menu.rows)) {
            for (column in (1..9)) {
                val bukkit = MenuUtils.rowColumnToSlot(row, column)

                val slot = menu.getSlot(row, column, player, menu)
                val renderedItem = slot.getItemStack(player)

                if (slot.isCaptive) {
                    val itemStack = inventory.getItem(bukkit) ?: continue

                    if (slot.isNotCaptiveFor(player)) {
                        continue
                    }

                    if (!slot.isCaptiveFromEmpty) {
                        if (itemStack == renderedItem) {
                            continue
                        }
                    }

                    if (itemStack.type.isAir || itemStack.amount == 0) {
                        continue
                    }

                    captiveItems.add(itemStack)
                } else {
                    inventory.setItem(bukkit, renderedItem)
                }
            }
        }

        menu.runOnRender(player)
    }
}
