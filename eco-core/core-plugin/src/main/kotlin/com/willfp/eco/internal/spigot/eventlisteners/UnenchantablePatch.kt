package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.internal.items.ArgParserUnenchantable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.persistence.PersistentDataType

object UnenchantablePatch : Listener {
    @EventHandler
    fun onPrepareEnchant(event: PrepareItemEnchantEvent) {
        val meta = event.item.itemMeta ?: return
        if (!meta.persistentDataContainer.has(ArgParserUnenchantable.key, PersistentDataType.BOOLEAN)) return
        event.isCancelled = true
    }

    // PrepareAnvilEvent is unreliable when plugins like EcoEnchants recalculate the result
    // asynchronously at HIGHEST priority. Block at click time instead, where the async
    // task has already completed and the final result is known.
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onAnvilClick(event: InventoryClickEvent) {
        if (event.rawSlot != 2) return
        val inventory = event.view.topInventory as? AnvilInventory ?: return

        val left = inventory.getItem(0) ?: return
        val leftMeta = left.itemMeta ?: return
        if (!leftMeta.persistentDataContainer.has(ArgParserUnenchantable.key, PersistentDataType.BOOLEAN)) return

        val result = inventory.getItem(2) ?: return
        val resultMeta = result.itemMeta ?: return

        val leftEnchants = if (leftMeta is EnchantmentStorageMeta) leftMeta.storedEnchants else leftMeta.enchants
        val resultEnchants = if (resultMeta is EnchantmentStorageMeta) resultMeta.storedEnchants else resultMeta.enchants

        if (leftEnchants != resultEnchants) {
            event.isCancelled = true
            inventory.setItem(2, null)
        }
    }
}