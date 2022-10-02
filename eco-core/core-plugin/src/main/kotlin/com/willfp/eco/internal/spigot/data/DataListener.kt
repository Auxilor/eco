package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeave(event: PlayerQuitEvent) {
        Eco.get().unloadPlayerProfile(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.runLater(5) {
            PlayerUtils.updateSavedDisplayName(event.player)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {
        Eco.get().unloadPlayerProfile(event.player.uniqueId)
    }
}
