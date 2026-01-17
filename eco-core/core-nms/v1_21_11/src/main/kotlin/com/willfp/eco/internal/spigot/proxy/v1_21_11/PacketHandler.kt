package com.willfp.eco.internal.spigot.proxy.v1_21_11

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxies.PacketHandlerProxy
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketHeldItemSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketSetSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.clearFrames
import com.willfp.eco.internal.spigot.proxy.v1_21_11.packet.NewItemsPacketOpenWindowMerchant
import com.willfp.eco.internal.spigot.proxy.v1_21_11.packet.NewItemsPacketSetCreativeSlot
import com.willfp.eco.internal.spigot.proxy.v1_21_11.packet.NewItemsPacketWindowItems
import com.willfp.eco.internal.spigot.proxy.v1_21_11.packet.PacketContainerClick
import com.willfp.eco.internal.spigot.proxy.v1_21_11.packet.PacketSetCursorItem
import net.minecraft.network.protocol.Packet
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player

class PacketHandler : PacketHandlerProxy {
    override fun sendPacket(player: Player, packet: com.willfp.eco.core.packet.Packet) {
        if (player !is CraftPlayer) {
            return
        }

        val handle = packet.handle

        if (handle !is Packet<*>) {
            return
        }

        player.handle.connection.send(handle)
    }

    override fun clearDisplayFrames() {
        clearFrames()
    }

    override fun getPacketListeners(plugin: EcoPlugin): List<PacketListener> {
        return listOf(
            PacketHeldItemSlot,
            NewItemsPacketOpenWindowMerchant,
            NewItemsPacketSetCreativeSlot,
            PacketSetSlot,
            NewItemsPacketWindowItems(plugin),
            PacketContainerClick,
            PacketSetCursorItem
        )
    }
}
