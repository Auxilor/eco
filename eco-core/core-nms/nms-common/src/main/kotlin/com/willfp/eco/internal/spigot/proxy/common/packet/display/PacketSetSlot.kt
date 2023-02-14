package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket

object PacketSetSlot : PacketListener {
    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundContainerSetSlotPacket ?: return

        Display.display(packet.item.asBukkitStack(), event.player)

        event.player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
