package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.util.PlayerUtils
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        PlayerUtils.updateSavedDisplayName(event.player)
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
        Eco.getHandler().ecoPlugin.logger.info("Player ${event.player.name} Quit (Saving)")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        PlayerUtils.updateSavedDisplayName(event.player)
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
        Eco.getHandler().ecoPlugin.logger.info("Player ${event.player.name} Logged In (Saving)")
    }
}
