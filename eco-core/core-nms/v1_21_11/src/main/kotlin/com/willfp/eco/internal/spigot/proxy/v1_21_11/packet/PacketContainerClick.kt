package com.willfp.eco.internal.spigot.proxy.v1_21_11.packet

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.HashedStack
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import org.bukkit.craftbukkit.inventory.CraftItemStack

object PacketContainerClick : PacketListener {
    override fun onReceive(event: PacketEvent) {
        val packet = event.packet.handle as? ServerboundContainerClickPacket ?: return
        val player = event.player

        val actualItem = packet.carriedItem() as? HashedStack.ActualItem ?: return

        Display.revert(CraftItemStack.asNewCraftStack(actualItem.item.value()))
        player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
