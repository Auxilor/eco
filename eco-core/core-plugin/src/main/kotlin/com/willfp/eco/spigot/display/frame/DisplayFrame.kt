package com.willfp.eco.spigot.display.frame

import org.bukkit.entity.Player
import java.util.*

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
}

val frames = mutableMapOf<UUID, DisplayFrame>()

var Player.lastDisplayFrame: DisplayFrame
    get() {
        return frames[this.uniqueId] ?: DisplayFrame(emptyMap())
    }
    set(value) {
        frames[this.uniqueId] = value
    }
