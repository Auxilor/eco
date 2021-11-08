package com.willfp.eco.spigot.data

import com.willfp.eco.core.Eco
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

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

    companion object {
        fun register() {
            ProxyServer.getInstance().pluginManager.registerListener(
                null, BungeeDataListener()
            )
        }
    }
}