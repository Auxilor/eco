package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket

object PacketHeldItemSlot : PacketListener {
    override fun onReceive(event: PacketEvent) {
        if (event.packet.handle !is ServerboundSetCarriedItemPacket) {
            return
        }

        event.player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
