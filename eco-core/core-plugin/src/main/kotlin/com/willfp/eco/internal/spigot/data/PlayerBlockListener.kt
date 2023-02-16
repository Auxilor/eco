package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.BlockUtils
import com.willfp.eco.util.PlayerUtils
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.StructureGrowEvent
import org.bukkit.persistence.PersistentDataType

class PlayerBlockListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.blockPlaced
        val player = event.player

        writeKey(block, PlayerUtils.getLocalID(player))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockMultiPlaceEvent) {
        val block = event.blockPlaced
        val player = event.player

        writeKey(block, PlayerUtils.getLocalID(player))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        this.plugin.scheduler.run {
            removeKey(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGrow(event: StructureGrowEvent) {
        val block = event.location.block

        this.plugin.scheduler.run {
            removeKey(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExtend(event: BlockPistonExtendEvent) {
        val locs = mutableListOf<Location>()
        val placers = mutableListOf<Int>()
        val toRemove = mutableListOf<Location>()

        for (block in event.blocks) {
            val placedBy = BlockUtils.placedByID(block)
            if (placedBy != 0) {
                locs.add(block.getRelative(event.direction).location)
                placers.add(placedBy)
                toRemove.add(block.location)
            }
        }

        this.plugin.scheduler.run {
            for (loc in toRemove) {
                removeKey(loc)
            }

            for (idx in locs.indices) {
                writeKey(locs[idx], placers[idx])
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onRetract(event: BlockPistonRetractEvent) {
        val locs = mutableListOf<Location>()
        val placers = mutableListOf<Int>()
        val toRemove = mutableListOf<Location>()

        for (block in event.blocks) {
            val placedBy = BlockUtils.placedByID(block)
            if (placedBy != 0) {
                locs.add(block.getRelative(event.direction).location)
                placers.add(placedBy)
                toRemove.add(block.location)
            }
        }

        this.plugin.scheduler.run {
            for (loc in toRemove) {
                removeKey(loc)
            }

            for (idx in locs.indices) {
                writeKey(locs[idx], placers[idx])
            }
        }
    }

    private fun writeKey(block: Block, placerID: Int) {
        writeKey(block.location, placerID)
    }

    private fun writeKey(location: Location, placerID: Int) {
        val loc = location.hashCode().toString(16)
        location.chunk.persistentDataContainer.set(
            plugin.createNamespacedKey(loc.lowercase()),
            PersistentDataType.INTEGER,
            placerID
        )
    }

    private fun removeKey(block: Block) {
        removeKey(block.location)
    }

    private fun removeKey(location: Location) {
        val loc = location.hashCode().toString(16)
        location.chunk.persistentDataContainer.remove(plugin.createNamespacedKey(loc.lowercase()))
    }
}