package com.willfp.eco.internal.spigot.proxy.common.packet

import com.willfp.eco.internal.spigot.proxy.common.toNMS
import io.netty.channel.Channel
import java.lang.reflect.Field
import net.minecraft.network.Connection
import net.minecraft.server.network.ServerCommonPacketListenerImpl
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

private const val BASE_NAME = "packet_handler"
private const val ECO_NAME = "eco_packets"

// Read reflectively: a direct read of the protected field throws IllegalAccessError on Spigot.
private val connectionField: Field =
    generateSequence<Class<*>>(ServerCommonPacketListenerImpl::class.java) { it.superclass }
        .flatMap { it.declaredFields.asSequence() }
        .first { Connection::class.java.isAssignableFrom(it.type) }
        .apply { isAccessible = true }

private val Player.channel: Channel
    get() = (connectionField.get(this.toNMS().connection) as Connection).channel

object PacketInjectorListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player

        val channel = player.channel

        if (BASE_NAME !in channel.pipeline().names()) {
            return
        }

        if (ECO_NAME in channel.pipeline().names()) {
            return
        }

        channel.pipeline().addBefore(BASE_NAME, ECO_NAME, EcoChannelDuplexHandler(player.uniqueId))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val player = event.player

        val channel = player.channel

        channel.eventLoop().submit {
            if (channel.pipeline().get(ECO_NAME) != null) {
                channel.pipeline().remove(ECO_NAME)
            }
        }
    }
}
