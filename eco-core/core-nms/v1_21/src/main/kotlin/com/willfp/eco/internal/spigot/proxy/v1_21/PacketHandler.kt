package com.willfp.eco.internal.spigot.proxy.v1_21

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.PacketHandlerProxy
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketAutoRecipe
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketHeldItemSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketSetSlot
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketWindowItems
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.clearFrames
import com.willfp.eco.internal.spigot.proxy.v1_21.packet.NewItemsPacketOpenWindowMerchant
import com.willfp.eco.internal.spigot.proxy.v1_21.packet.NewItemsPacketSetCreativeSlot
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
            PacketAutoRecipe(plugin),
            PacketHeldItemSlot,
            NewItemsPacketOpenWindowMerchant,
            NewItemsPacketSetCreativeSlot,
            PacketSetSlot,
            PacketWindowItems(plugin)
        )
    }
}
