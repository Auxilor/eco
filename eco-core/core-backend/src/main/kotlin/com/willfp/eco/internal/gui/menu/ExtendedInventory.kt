package com.willfp.eco.internal.gui.menu

import com.willfp.eco.internal.gui.slot.EcoCaptiveSlot
import com.willfp.eco.util.MenuUtils
import com.willfp.eco.util.StringUtils
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class ExtendedInventory(
    val inventory: Inventory,
    private val menu: EcoMenu
) {
    val captiveItems = mutableListOf<ItemStack>()
    val data = mutableMapOf<NamespacedKey, Any>()

    fun refresh(player: Player) {
        captiveItems.clear()
        for (i in 0 until inventory.size) {
            val (row, column) = MenuUtils.convertSlotToRowColumn(i)
            val slot = menu.getSlot(row, column)
            if (slot is EcoCaptiveSlot) {
                val defaultItem = slot.getItemStack(player)
                val item = inventory.getItem(i) ?: continue
                if (item != defaultItem) {
                    captiveItems.add(item)
                }
            }
        }

        var i = 0
        for (row in menu.slots) {
            for (slot in row) {
                if (i == menu.rows * 9) {
                    break
                }
                val slotItem = slot.getItemStack(player, menu)
                val meta = slotItem.itemMeta
                if (meta != null) {
                    val lore = meta.lore
                    if (lore != null) {
                        lore.replaceAll{ s -> StringUtils.format(s, player) }
                        meta.lore = lore
                    }
                    slotItem.itemMeta = meta
                }
                if (!slot.isCaptive) {
                    inventory.setItem(i, slotItem)
                }
                i++
            }
        }
    }
}