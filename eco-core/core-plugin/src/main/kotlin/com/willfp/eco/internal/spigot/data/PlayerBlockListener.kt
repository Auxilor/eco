package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.BlockUtils
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
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

        this.plugin.scheduler.at(block.location).run {
            removeKey(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onGrow(event: StructureGrowEvent) {
        val block = event.location.block

        this.plugin.scheduler.at(block.location).run {
            removeKey(block)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onExtend(event: BlockPistonExtendEvent) {
        handlePiston(event.blocks, event.direction)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onRetract(event: BlockPistonRetractEvent) {
        handlePiston(event.blocks, event.direction)
    }

    /**
     * A piston can push blocks across a chunk, and so across a region, boundary, so work is
     * batched per chunk rather than per block: a chunk belongs to exactly one region, so one
     * task per chunk is always region-legal, and it keeps this to roughly one or two tasks
     * per piston event instead of two per moved block.
     *
     * Within a single block's handling, `removeKey(from)` is ordered before `writeKey(to)`
     * whenever `from` and `to` share a chunk, because both land in that chunk's task in that
     * order. When a block's move crosses a chunk boundary, the remove and the write land in
     * two different chunk tasks and that ordering is not guaranteed between them.
     */
    private fun handlePiston(blocks: List<Block>, direction: BlockFace) {
        val chunkOps = LinkedHashMap<Triple<World, Int, Int>, MutableList<Runnable>>()

        for (block in blocks) {
            if (!BlockUtils.isPlayerPlaced(block)) {
                continue
            }

            val from = block
            val to = block.getRelative(direction)

            val fromChunk = Triple(from.world, from.x shr 4, from.z shr 4)
            val toChunk = Triple(to.world, to.x shr 4, to.z shr 4)

            chunkOps.getOrPut(fromChunk) { mutableListOf() }.add(Runnable { removeKey(from) })
            chunkOps.getOrPut(toChunk) { mutableListOf() }.add(Runnable { writeKey(to) })
        }

        for ((chunk, ops) in chunkOps) {
            val (world, chunkX, chunkZ) = chunk

            this.plugin.scheduler.at(world, chunkX, chunkZ).run {
                for (op in ops) {
                    op.run()
                }
            }
        }
    }

    private fun writeKey(block: Block) {
        writeKey(block.location)
    }

    private fun writeKey(location: Location) {
        val loc = location.hashCode().toString(16)
        location.chunk.persistentDataContainer.set(
            plugin.createNamespacedKey(loc.lowercase()),
            PersistentDataType.INTEGER,
            1
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