package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.internal.spigot.proxy.RecipePacketProxy
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket

class RecipePacket : RecipePacketProxy {
    override fun splitPacket(packet: Any): List<Any> {
        packet as ClientboundUpdateRecipesPacket

        val packets = mutableListOf<Any>()

        for (list in packet.recipes.chunked(64)) {
            packets.add(ClientboundUpdateRecipesPacket(list))
        }

        return packets
    }
}
