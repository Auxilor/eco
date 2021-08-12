package com.willfp.eco.internal.gui.slot

import com.willfp.eco.core.Eco
import com.willfp.eco.core.gui.slot.Slot
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.function.BiConsumer

class EcoCaptivatorSlot : EcoSlot(
    { ItemStack(Material.AIR) },
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem,
    allowMovingItem
) {
    companion object {
        val plugin = Eco.getHandler().ecoPlugin!!

        val allowMovingItem: BiConsumer<InventoryClickEvent, Slot> = BiConsumer { event, slot ->
            val player = event.whoClicked
            if (player !is Player) {
                return@BiConsumer
            }

            if (slot !is EcoSlot) {
                return@BiConsumer
            }

            if (event.currentItem != slot.getItemStack(player)
                && event.cursor != null && event.cursor!!.type == Material.AIR) {
                event.isCancelled = false
            }
        }
    }
}