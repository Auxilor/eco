package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.core.data.PlayerProfile
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
        Eco.getHandler().ecoPlugin.logger.info("Profile before leave: ${PlayerProfile.load(event.player)}")
        Eco.getHandler().playerProfileHandler.savePlayerBlocking(event.player.uniqueId)
        Eco.getHandler().playerProfileHandler.unloadPlayer(event.player.uniqueId)
        Eco.getHandler().ecoPlugin.logger.info("Player ${event.player.name} Quit (Saving)")
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        Eco.getHandler().playerProfileHandler.unloadPlayer(event.player.uniqueId)
        PlayerUtils.updateSavedDisplayName(event.player)
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        Eco.getHandler().playerProfileHandler.unloadPlayer(event.player.uniqueId)
        Eco.getHandler().ecoPlugin.logger.info("Player ${event.player.name} Logged In (Unloaded Profile)")
        Eco.getHandler().ecoPlugin.logger.info("Profile after join: ${PlayerProfile.load(event.player)}")
    }
}
