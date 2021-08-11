package com.willfp.eco.internal.gui.menu

import com.willfp.eco.internal.gui.slot.EcoCaptivatorSlot
import com.willfp.eco.util.MenuUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ExtendedInventory(
    val inventory: Inventory,
    private val menu: EcoMenu
) {
    val captiveItems: MutableList<ItemStack> = ArrayList()

    fun refresh(player: Player) {
        captiveItems.clear()
        for (i in 0 until inventory.size) {
            val pair = MenuUtils.convertSlotToRowColumn(i);
            val row = pair.first!!
            val column = pair.second!!
            val slot = menu.getSlot(row, column)
            if (slot is EcoCaptivatorSlot) {
                val defaultItem = slot.getItemStack(player)
                val item = inventory.getItem(i) ?: continue
                if (item != defaultItem) {
                    captiveItems.add(item)
                }
            }
        }
    }
}