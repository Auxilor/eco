package com.willfp.eco.internal.spigot.proxy.common.packet.display.frame

import com.willfp.eco.core.items.HashedItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class DisplayFrame(val items: Array<HashedItem?>) {
    fun getChangedSlots(newFrame: DisplayFrame): IntArray {
        val maxSize = maxOf(items.size, newFrame.items.size)
        val changes = IntArray(maxSize)
        var count = 0

        for (slot in 0 until maxSize) {
            val oldHash = items.getOrNull(slot)?.hash
            val newHash = newFrame.items.getOrNull(slot)?.hash
            if (oldHash != newHash) {
                changes[count++] = slot
            }
        }

        return changes.copyOf(count)
    }

    fun getItem(slot: Int): ItemStack? {
        return items.getOrNull(slot)?.item
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DisplayFrame) return false
        return items.contentEquals(other.items)
    }

    override fun hashCode(): Int {
        return items.contentHashCode()
    }

    companion object {
        val EMPTY = DisplayFrame(emptyArray())
    }
}

private val frames = ConcurrentHashMap<UUID, DisplayFrame>()

var Player.lastDisplayFrame: DisplayFrame
    get() {
        return frames[this.uniqueId] ?: DisplayFrame.EMPTY
    }
    set(value) {
        frames[this.uniqueId] = value
    }

fun clearFrames() {
    frames.clear()
}

fun removeFrame(uuid: UUID) {
    frames.remove(uuid)
}
