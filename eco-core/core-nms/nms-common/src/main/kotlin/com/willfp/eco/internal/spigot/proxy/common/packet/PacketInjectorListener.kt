package com.willfp.eco.internal.spigot.proxy.common.packet

import com.willfp.eco.internal.spigot.proxy.common.toNMS
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object PacketInjectorListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val channel = player.toNMS().connection.connection.channel

        channel.pipeline().addBefore("packet_handler", "eco_packets", EcoChannelDuplexHandler(player.uniqueId))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player

        val channel = player.toNMS().connection.connection.channel

        channel.eventLoop().submit {
            if (channel.pipeline().get("eco_packets") != null) {
                channel.pipeline().remove("eco_packets")
            }
        }
    }
}
