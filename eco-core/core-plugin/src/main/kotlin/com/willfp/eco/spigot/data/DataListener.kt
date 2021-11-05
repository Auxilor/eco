package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import com.willfp.eco.util.PlayerUtils
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class DataListener : Listener {
    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        PlayerUtils.updateSavedDisplayName(event.player)
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
        PlayerUtils.updateSavedDisplayName(event.player)
    }
}

class BungeeDataListener : Listener {
    @EventHandler
    fun onConnected(event: ServerConnectedEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onSwitch(event: ServerSwitchEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }
}