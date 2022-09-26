package com.willfp.eco.internal.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.spigot.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.display.frame.lastDisplayFrame
import org.bukkit.entity.Player

class PacketHeldItemSlot(plugin: EcoPlugin) :
    AbstractPacketAdapter(plugin, PacketType.Play.Server.HELD_ITEM_SLOT, false) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        player.lastDisplayFrame = DisplayFrame.EMPTY
    }

    override fun onReceive(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
