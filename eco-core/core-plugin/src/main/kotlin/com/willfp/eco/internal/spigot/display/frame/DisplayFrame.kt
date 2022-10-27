package com.willfp.eco.internal.spigot.display.frame

import com.comphenix.protocol.injector.temporary.TemporaryPlayer
import com.willfp.eco.core.items.HashedItem
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class DisplayFrame(val items: Map<Byte, HashedItem>) {
    fun getChangedSlots(newFrame: DisplayFrame): List<Byte> {
        val changes = mutableListOf<Byte>()

        for ((slot, data) in newFrame.items) {
            if (items[slot]?.hash != data.hash) {
                changes.add(slot)
            }
        }

        return changes
    }

    fun getItem(slot: Byte): ItemStack? {
        return items[slot]?.item
    }

    companion object {
        val EMPTY = DisplayFrame(emptyMap())
    }
}

private val frames = ConcurrentHashMap<UUID, DisplayFrame>()

var Player.lastDisplayFrame: DisplayFrame
    get() {
        // ProtocolLib fix
        if (this is TemporaryPlayer) {
            return DisplayFrame.EMPTY
        }

        return frames[this.uniqueId] ?: DisplayFrame.EMPTY
    }
    set(value) {
        // ProtocolLib fix
        if (this is TemporaryPlayer) {
            return
        }

        frames[this.uniqueId] = value
    }

fun clearFrames() {
    frames.clear()
}