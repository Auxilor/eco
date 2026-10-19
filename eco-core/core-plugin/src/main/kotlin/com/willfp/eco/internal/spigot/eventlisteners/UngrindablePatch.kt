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
        if (event.inventory.hasUngrindableInput()) {
            event.result = null
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onGrindstoneClick(event: InventoryClickEvent) {
        if (event.rawSlot != 2) return
        val inventory = event.view.topInventory as? GrindstoneInventory ?: return

        if (inventory.hasUngrindableInput()) {
            event.isCancelled = true
            inventory.setItem(2, null)
        }
    }

    private fun GrindstoneInventory.hasUngrindableInput(): Boolean =
        upperItem.isUngrindable() || lowerItem.isUngrindable()

    private fun ItemStack?.isUngrindable(): Boolean =
        this?.itemMeta?.persistentDataContainer?.has(ArgParserUngrindable.key, PersistentDataType.BOOLEAN) == true
}
