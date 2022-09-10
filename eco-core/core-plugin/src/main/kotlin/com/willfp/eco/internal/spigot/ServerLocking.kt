package com.willfp.eco.internal.spigot

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

object ServerLocking : Listener {
    private var lockReason: String? = null

    @Suppress("DEPRECATION")
    @EventHandler
    fun handle(event: PlayerLoginEvent) {
        if (lockReason != null) {
            event.disallow(
                PlayerLoginEvent.Result.KICK_OTHER,
                lockReason!!
            )
        }
    }

    fun lock(reason: String) {
        lockReason = reason
    }

    fun unlock() {
        lockReason = null
    }
}
