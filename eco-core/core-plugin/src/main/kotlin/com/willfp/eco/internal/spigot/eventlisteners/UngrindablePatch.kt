package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.internal.items.ArgParserUngrindable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareGrindstoneEvent
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

object UngrindablePatch : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPrepareGrindstone(event: PrepareGrindstoneEvent) {
        if (hasUngrindableInput(event.inventory)) {
            event.result = null
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onGrindstoneClick(event: InventoryClickEvent) {
        if (event.rawSlot != 2) return
        val inventory = event.view.topInventory as? GrindstoneInventory ?: return

        if (hasUngrindableInput(inventory)) {
            event.isCancelled = true
            inventory.setItem(2, null)
        }
    }

    private fun hasUngrindableInput(inventory: GrindstoneInventory): Boolean =
        isUngrindable(inventory.upperItem) || isUngrindable(inventory.lowerItem)

    private fun isUngrindable(item: ItemStack?): Boolean {
        val meta = item?.itemMeta ?: return false
        return meta.persistentDataContainer.has(ArgParserUngrindable.key, PersistentDataType.BOOLEAN)
    }
}
