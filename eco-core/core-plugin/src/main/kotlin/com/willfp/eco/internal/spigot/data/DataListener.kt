package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        Eco.getHandler().playerProfileHandler.unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.runLater({
            PlayerUtils.updateSavedDisplayName(event.player)
        }, 5)
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        Eco.getHandler().playerProfileHandler.unloadPlayer(event.player.uniqueId)
    }
}
