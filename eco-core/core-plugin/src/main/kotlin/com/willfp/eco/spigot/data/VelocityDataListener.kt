package com.willfp.eco.spigot.data

import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.willfp.eco.core.Eco
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class VelocityDataListener : Listener {
    @EventHandler
    fun onConnected(event: ServerConnectedEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onDisconnect(event: DisconnectEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }
}
