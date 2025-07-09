package com.willfp.eco.internal.spigot.proxy.v1_21_7.packet

import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.HashedStack
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket

object PacketContainerClick : PacketListener {
    // Displayed hash -> original, required to avoid ghost items in cursor
    private val originals = hashMapOf<Int, HashedStack>()

    fun map(original: HashedStack, displayed: Int) {
        originals[displayed] = original
    }

    override fun onReceive(event: PacketEvent) {
        val packet = event.packet.handle as? ServerboundContainerClickPacket ?: return

        val carried = packet.carriedItem as? HashedStack.ActualItem ?: return
        val player = event.player
        val original = originals[carried.hash()] ?: return

        event.packet.handle = ServerboundContainerClickPacket(
            packet.containerId,
            packet.stateId,
            packet.slotNum,
            packet.buttonNum,
            packet.clickType,
            packet.changedSlots,
            original
        )

        player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
