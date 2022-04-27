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

    fun render() {
        generateCaptive()

        var i = 0
        for (row in menu.slots) {
            for (slot in row) {
                if (i == menu.rows * 9) {
                    break
                }

                if (!slot.isCaptive) {
                    inventory.setItem(i, slot.getItemStack(player, menu))
                }

                i++
            }
        }

        menu.runOnRender(player)
    }

    private fun generateCaptive() {
        captiveItems.clear()
        for (i in 0 until inventory.size) {
            val (row, column) = MenuUtils.convertSlotToRowColumn(i)
            val slot = menu.getSlot(row, column)
            if (slot.isCaptive) {
                val renderedItem = slot.getItemStack(player)
                val itemStack = inventory.getItem(i) ?: continue

                if (itemStack == renderedItem || itemStack.type.isAir || itemStack.amount == 0) {
                    continue
                }

                captiveItems.add(itemStack)
            }
        }
    }
}
