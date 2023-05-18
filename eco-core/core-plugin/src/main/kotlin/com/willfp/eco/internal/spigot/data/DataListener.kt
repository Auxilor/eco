package com.willfp.eco.internal.spigot.data

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener(
    private val plugin: EcoPlugin,
    private val handler: ProfileHandler
) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeave(event: PlayerQuitEvent) {
        val profile = handler.accessLoadedProfile(event.player.uniqueId) ?: return
        handler.saveKeysFor(event.player.uniqueId, profile.data.keys)
        handler.unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.runLater(5) {
            PlayerUtils.updateSavedDisplayName(event.player)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {
        handler.unloadPlayer(event.player.uniqueId)
    }
}
