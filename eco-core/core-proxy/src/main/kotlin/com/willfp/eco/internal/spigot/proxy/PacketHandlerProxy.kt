package com.willfp.eco.internal.spigot.proxy

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.packet.PacketListener
import org.bukkit.entity.Player

interface PacketHandlerProxy {
    fun addPlayer(player: Player)

    fun removePlayer(player: Player)

    fun sendPacket(player: Player, packet: Any)

    fun clearDisplayFrames()

    fun getPacketListeners(plugin: EcoPlugin): List<PacketListener>
}
