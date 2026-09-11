package com.willfp.eco.internal.spigot.data.profiles

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class ProfileLoadListener(
    private val plugin: EcoPlugin,
    private val handler: ProfileHandler
) : Listener {
    /**
     * Carry a joining player's profile out of data.yml, if a migration is still running.
     *
     * This event is off the main thread and fires before anything can read the profile, so the one
     * blocking copy a player costs happens while they are still connecting.
     */
    @EventHandler
    fun onPreLogin(event: AsyncPlayerPreLoginEvent) {
        handler.ensureMigrated(event.uniqueId)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLogin(event: PlayerLoginEvent) {
        handler.unloadPlayer(event.player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onLeave(event: PlayerQuitEvent) {
        handler.unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.scheduler.on(event.player).runLater(5) {
            PlayerUtils.updateSavedDisplayName(event.player)
        }
    }
}
