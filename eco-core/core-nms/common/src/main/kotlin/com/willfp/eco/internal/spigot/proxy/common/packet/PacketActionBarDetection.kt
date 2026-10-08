package com.willfp.eco.internal.spigot.proxy.common.packet

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.actionbar.PersistentActionBarService
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket

class PacketActionBarDetection(
    private val service: PersistentActionBarService
) : PacketListener {
    override fun onSend(event: PacketEvent) {
        when (val packet = event.packet.handle) {
            is ClientboundSetActionBarTextPacket -> service.onForeignActionBar(event.player)
            is ClientboundSystemChatPacket -> if (packet.overlay) service.onForeignActionBar(event.player)
        }
    }
}
