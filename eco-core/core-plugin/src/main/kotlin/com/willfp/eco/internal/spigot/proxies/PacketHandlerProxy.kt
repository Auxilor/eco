package com.willfp.eco.internal.spigot.proxies

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.Packet
import com.willfp.eco.core.packet.PacketListener
import org.bukkit.entity.Player

interface PacketHandlerProxy {
    fun sendPacket(player: Player, packet: Packet)

    fun clearDisplayFrames()

    fun getPacketListeners(plugin: EcoPlugin): List<PacketListener>
}
