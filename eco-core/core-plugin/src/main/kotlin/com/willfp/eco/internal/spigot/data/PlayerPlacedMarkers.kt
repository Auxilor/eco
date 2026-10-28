package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.BlockUtils
import org.bukkit.Chunk
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.persistence.PersistentDataType

private const val CHUNK_MASK = 15
private const val HEX_RADIX = 16

/**
 * The player-placed markers eco stores in each chunk's persistent data container.
 *
 * Markers are keyed by the block's position within its chunk. Markers written by older
 * versions are keyed by the location hash, and are removed alongside the new ones.
 */
class PlayerPlacedMarkers(
    private val plugin: EcoPlugin
) {
    fun mark(block: Block) {
        block.chunk.persistentDataContainer.set(block.placedKey(), PersistentDataType.INTEGER, 1)
    }

    fun unmark(block: Block) {
        val container = block.chunk.persistentDataContainer

        container.remove(block.placedKey())
        container.remove(block.legacyPlacedKey())
    }

    /**
     * Removal waits a tick, as with a break, so that listeners handling the same event still
     * see the blocks as player-placed. The blocks can span chunks, and so regions, so work is
     * batched into one task per chunk.
     */
    fun unmarkLater(blocks: Collection<Block>) {
        val chunkOps = LinkedHashMap<Chunk, MutableList<Runnable>>()

        for (block in blocks) {
            chunkOps.getOrPut(block.chunk) { mutableListOf() }.add(Runnable { unmark(block) })
        }

        runPerChunk(chunkOps)
    }

    /**
     * A piston can push blocks across a chunk, and so across a region, boundary, so work is
     * batched per chunk rather than per block: a chunk belongs to exactly one region, so one
     * task per chunk is always region-legal, and it keeps this to roughly one or two tasks
     * per piston event instead of two per moved block.
     *
     * Within a single block's handling, `unmark(from)` is ordered before `mark(to)`
     * whenever `from` and `to` share a chunk, because both land in that chunk's task in that
     * order. When a block's move crosses a chunk boundary, the remove and the write land in
     * two different chunk tasks and that ordering is not guaranteed between them.
     */
    fun moveLater(blocks: List<Block>, direction: BlockFace) {
        val chunkOps = LinkedHashMap<Chunk, MutableList<Runnable>>()

        for (block in blocks) {
            if (!BlockUtils.isPlayerPlaced(block)) {
                continue
            }

            val to = block.getRelative(direction)

            chunkOps.getOrPut(block.chunk) { mutableListOf() }.add(Runnable { unmark(block) })
            chunkOps.getOrPut(to.chunk) { mutableListOf() }.add(Runnable { mark(to) })
        }

        runPerChunk(chunkOps)
    }

    private fun runPerChunk(chunkOps: Map<Chunk, List<Runnable>>) {
        for ((chunk, ops) in chunkOps) {
            this.plugin.scheduler.at(chunk.world, chunk.x, chunk.z).run {
                for (op in ops) {
                    op.run()
                }
            }
        }
    }

    private fun Block.placedKey(): NamespacedKey =
        plugin.createNamespacedKey("placed_${this.x and CHUNK_MASK}_${this.y}_${this.z and CHUNK_MASK}")

    private fun Block.legacyPlacedKey(): NamespacedKey =
        plugin.createNamespacedKey(this.location.hashCode().toString(HEX_RADIX).lowercase())
}
