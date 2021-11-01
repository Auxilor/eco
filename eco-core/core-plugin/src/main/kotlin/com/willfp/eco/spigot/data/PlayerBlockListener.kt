package com.willfp.eco.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NamespacedKeyUtils
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType

class PlayerBlockListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced

        writeKey(block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockMultiPlaceEvent) {
        val block = event.blockPlaced

        writeKey(block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        this.plugin.scheduler.run {
            removeKey(block)
        }
    }

    private fun writeKey(block: Block) {
        val loc = block.location.hashCode().toString(16)
        block.chunk.persistentDataContainer.set(
            plugin.namespacedKeyFactory.create(loc.lowercase()),
            PersistentDataType.INTEGER,
            1
        )
    }

    private fun removeKey(block: Block) {
        val loc = block.location.hashCode().toString(16)
        block.chunk.persistentDataContainer.remove(plugin.namespacedKeyFactory.create(loc.lowercase()))
    }
}