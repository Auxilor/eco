package com.willfp.eco.internal.spigot.packet

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.proxy.PacketHandlerProxy
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PacketInjectorListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        plugin.getProxy(PacketHandlerProxy::class.java).addPlayer(event.player)
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        plugin.getProxy(PacketHandlerProxy::class.java).addPlayer(event.player)
    }
}
