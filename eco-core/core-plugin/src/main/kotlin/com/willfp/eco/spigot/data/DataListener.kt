package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        PlayerUtils.updateSavedDisplayName(event.player)
        Eco.getHandler().playerProfileHandler.savePlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        PlayerUtils.updateSavedDisplayName(event.player)
    }
}