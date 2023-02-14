package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.PacketHandlerProxy
import com.willfp.eco.internal.spigot.proxy.common.packet.EcoChannelDuplexHandler
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketAutoRecipe
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketHeldItemSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketOpenWindowMerchant
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketSetCreativeSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketSetSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketWindowItems
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.clearFrames
import com.willfp.eco.internal.spigot.proxy.v1_18_R1.display.PacketChat
import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketHandler: PacketHandlerProxy {
    override fun addPlayer(player: Player) {
        if (player !is CraftPlayer) {
            return
        }

        player.handle.connection.connection.channel.pipeline()
            .addBefore("eco_packet_handler", player.name, EcoChannelDuplexHandler(player.uniqueId))
    }

    override fun removePlayer(player: Player) {
        if (player !is CraftPlayer) {
            return
        }

        val channel = player.handle.connection.connection.channel

        channel.eventLoop().submit {
            channel.pipeline().remove(player.name)
        }
    }

    override fun sendPacket(player: Player, packet: Any) {
        if (player !is CraftPlayer) {
            return
        }

        if (packet !is Packet<*>) {
            return
        }

        player.handle.connection.send(packet)
    }

    override fun clearDisplayFrames() {
        clearFrames()
    }

    override fun getPacketListeners(plugin: EcoPlugin): List<PacketListener> {
        return listOf(
            PacketAutoRecipe(plugin),
            PacketHeldItemSlot,
            PacketOpenWindowMerchant,
            PacketSetCreativeSlot,
            PacketSetSlot,
            PacketWindowItems(plugin),
            PacketChat
        )
    }
}
