package com.willfp.eco.spigot.display.frame

import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap

data class DisplayFrame(val items: Map<Byte, Int>) {
    fun getChangedSlots(newFrame: DisplayFrame): List<Byte> {
        val changes = mutableListOf<Byte>()

        for ((slot, hash) in newFrame.items) {
            if (items[slot] != hash) {
                changes.add(slot)
            }
        }

        return changes
    }

    companion object {
        val EMPTY = DisplayFrame(emptyMap())
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