package com.willfp.eco.internal.spigot.proxy.v1_21_4.packet

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket

object PacketSetCursorItem : PacketListener {
    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundSetCursorItemPacket ?: return
        val player = event.player
        Display.display(packet.contents.asBukkitStack(), player)
        player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
