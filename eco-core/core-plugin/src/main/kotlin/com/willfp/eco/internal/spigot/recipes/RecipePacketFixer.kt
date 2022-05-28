package com.willfp.eco.internal.spigot.recipes

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.proxy.RecipePacketProxy
import org.bukkit.entity.Player

class RecipePacketFixer(
    plugin: EcoPlugin
) : AbstractPacketAdapter(
    plugin,
    PacketType.Play.Server.RECIPE_UPDATE,
    false
) {
    override fun onSend(packet: PacketContainer, player: Player, event: PacketEvent) {
        val nmsPackets = this.getPlugin().getProxy(RecipePacketProxy::class.java).splitPacket(packet.handle)

        if (nmsPackets.size <= 1) {
            return
        }

        event.isCancelled = true
        for (nmsPacket in nmsPackets) {
            val protocolLibPacket = PacketContainer.fromPacket(nmsPacket)
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, protocolLibPacket, false)
        }
    }
}
