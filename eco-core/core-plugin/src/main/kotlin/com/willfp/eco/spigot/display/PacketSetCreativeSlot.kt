package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PacketSetCreativeSlot(plugin: EcoPlugin) :
    AbstractPacketAdapter(plugin, PacketType.Play.Client.SET_CREATIVE_SLOT, false) {
    override fun onReceive(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        packet.itemModifier.modify(0) { itemStack: ItemStack? ->
            Display.revert(
                itemStack!!
            )
        }
    }
}