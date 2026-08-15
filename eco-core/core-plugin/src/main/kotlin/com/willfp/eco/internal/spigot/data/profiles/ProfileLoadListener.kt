package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class ProfileLoadListener(
    private val plugin: EcoPlugin,
    private val handler: ProfileHandler
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {
        handler.unloadProfile(event.player.uniqueId)
        handler.unloadProfile(Eco.get().playerProfileResolver.resolve(event.player))
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeave(event: PlayerQuitEvent) {
        handler.unloadProfile(event.player.uniqueId)
        handler.unloadProfile(Eco.get().playerProfileResolver.resolve(event.player))
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.runLater(5) {
            PlayerUtils.updateSavedDisplayName(event.player)
        }
    }
}
