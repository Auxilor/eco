package com.willfp.eco.internal.gui.menu

import com.willfp.eco.util.MenuUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class MenuRenderedInventory(
    val menu: EcoMenu,
    val inventory: Inventory,
    val player: Player
) {
    val captiveItems = mutableListOf<ItemStack>()
    val state = mutableMapOf<String, Any?>()

    fun render(noSideEffects: Boolean = false) {
        if (!noSideEffects) {
            generateCaptive()
        }

        for (row in (1..menu.rows)) {
            for (column in (1..9)) {
                val bukkit = MenuUtils.rowColumnToSlot(row, column)
                val item = menu.getSlot(row, column).getItemStack(player)

                inventory.setItem(bukkit, item)
            }
        }

        if (!noSideEffects) {
            menu.runOnRender(player)
        }
    }

    fun generateCaptive() {
        captiveItems.clear()
        for (i in 0 until inventory.size) {
            val (row, column) = MenuUtils.convertSlotToRowColumn(i)
            val slot = menu.getSlot(row, column)
            if (slot.isCaptive) {
                val renderedItem = slot.getItemStack(player)
                val itemStack = inventory.getItem(i) ?: continue

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
            }
        }
    }
}
