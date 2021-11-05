package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BungeeDataListener : Listener {
    @EventHandler
    fun onConnected(event: ServerConnectedEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onDisconnect(event: ServerDisconnectEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }

    @EventHandler
    fun onSwitch(event: ServerSwitchEvent) {
        (Eco.getHandler().playerProfileHandler as EcoPlayerProfileHandler).unloadPlayer(event.player.uniqueId)
    }
}
