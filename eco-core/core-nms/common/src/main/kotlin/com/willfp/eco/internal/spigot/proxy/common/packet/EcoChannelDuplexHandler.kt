package com.willfp.eco.internal.spigot.proxy.common.packet

import com.willfp.eco.core.packet.Packet
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.internal.events.handleReceive
import com.willfp.eco.internal.events.handleSend
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import org.bukkit.Bukkit
import java.util.UUID

class EcoChannelDuplexHandler(
    private val uuid: UUID
) : ChannelDuplexHandler() {

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        val player = Bukkit.getPlayer(uuid)

        if (player != null) {
            val event = PacketEvent(Packet(msg), player)

            event.handleReceive()

            if (!event.isCancelled) {
                super.channelRead(ctx, event.packet.handle)
            }
        } else {
            super.channelRead(ctx, msg)
        }
    }

    override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
        val player = Bukkit.getPlayer(uuid)

        if (player != null) {
            val event = PacketEvent(Packet(msg), player)

            event.handleSend()

            if (!event.isCancelled) {
                super.write(ctx, event.packet.handle, promise)
            }
        } else {
            super.write(ctx, msg, promise)
        }
    }
}
