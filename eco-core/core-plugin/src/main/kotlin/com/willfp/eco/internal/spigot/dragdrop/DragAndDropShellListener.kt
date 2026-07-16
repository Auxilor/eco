package com.willfp.eco.internal.spigot.dragdrop

import com.willfp.eco.core.dragdrop.DragAndDropHandlers
import com.willfp.eco.core.dragdrop.DragAndDropResult
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack

class DragAndDropShellListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        if (DragAndDropHandlers.all().isEmpty()) return
        if (event.whoClicked.gameMode == GameMode.CREATIVE) return
        if (event.slotType == InventoryType.SlotType.RESULT) return

        val current = event.currentItem ?: return
        val cursor = event.cursor ?: return
        if (current.type == Material.AIR || cursor.type == Material.AIR) return

        val clickedInventory = event.clickedInventory ?: return
        val player = event.whoClicked as? Player ?: return

        for ((handler, settings) in DragAndDropHandlers.all()) {
            val inOwnInventory = clickedInventory == player.inventory
            val inCraftingNonResult = settings.allowCraftingTableNonResultSlot &&
                event.view.topInventory.type == InventoryType.CRAFTING &&
                event.slotType != InventoryType.SlotType.RESULT

            if (!inOwnInventory && !inCraftingNonResult) continue
            if (!handler.matches(cursor, current)) continue

            when (handler.apply(player, cursor, current)) {
                DragAndDropResult.APPLIED -> consumeCursor(player, cursor)
                DragAndDropResult.DENIED -> Unit
            }

            event.isCancelled = true
            return
        }
    }

    private fun consumeCursor(player: Player, cursor: ItemStack) {
        if (cursor.amount > 1) {
            cursor.amount -= 1
            player.setItemOnCursor(cursor)
        } else {
            player.setItemOnCursor(ItemStack(Material.AIR))
        }
    }
}
