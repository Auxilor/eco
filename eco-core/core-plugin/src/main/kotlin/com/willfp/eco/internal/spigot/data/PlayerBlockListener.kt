package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockMultiPlaceEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.block.BlockPistonRetractEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.world.StructureGrowEvent

class PlayerBlockListener(
    private val plugin: EcoPlugin,
    private val markers: PlayerPlacedMarkers
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockPlaceEvent) {
        markers.mark(event.blockPlaced)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlace(event: BlockMultiPlaceEvent) {
        markers.mark(event.blockPlaced)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        val block = event.block

        this.plugin.scheduler.at(block.location).run {
            markers.unmark(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGrow(event: StructureGrowEvent) {
        markers.unmarkLater(event.blocks.map { it.block } + event.location.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExtend(event: BlockPistonExtendEvent) {
        markers.moveLater(event.blocks, event.direction)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onRetract(event: BlockPistonRetractEvent) {
        markers.moveLater(event.blocks, event.direction)
    }
}
