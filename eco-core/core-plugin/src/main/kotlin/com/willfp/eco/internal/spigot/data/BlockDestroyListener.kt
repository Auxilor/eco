package com.willfp.eco.internal.spigot.data

import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockFormEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityExplodeEvent

/**
 * Clears player-placed markers for blocks removed by something other than a player.
 *
 * A block that forms or spreads into air or liquid replaces a block that left without an
 * event, such as one removed by another plugin, so any marker left there is stale.
 */
class BlockDestroyListener(
    private val markers: PlayerPlacedMarkers
) : Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExplode(event: BlockExplodeEvent) {
        markers.unmarkLater(event.blockList() + event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExplode(event: EntityExplodeEvent) {
        markers.unmarkLater(event.blockList())
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBurn(event: BlockBurnEvent) {
        markers.unmarkLater(listOf(event.block))
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onChange(event: EntityChangeBlockEvent) {
        if (event.to.isAir) {
            markers.unmarkLater(listOf(event.block))
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onForm(event: BlockFormEvent) {
        unmarkIfStale(event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onSpread(event: BlockSpreadEvent) {
        unmarkIfStale(event.block)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onFlow(event: BlockFromToEvent) {
        markers.unmark(event.toBlock)
    }

    private fun unmarkIfStale(block: Block) {
        if (block.type.isAir || block.isLiquid) {
            markers.unmark(block)
        }
    }
}
