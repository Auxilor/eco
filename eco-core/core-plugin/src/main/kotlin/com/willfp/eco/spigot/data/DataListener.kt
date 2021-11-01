package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class DataListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        Eco.getHandler().playerProfileHandler.savePlayer(event.player.uniqueId)
    }
}