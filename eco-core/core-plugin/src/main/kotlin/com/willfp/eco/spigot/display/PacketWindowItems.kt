package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer

class PacketWindowItems(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, false) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        packet.itemListModifier.modify(0) { itemStacks: List<ItemStack>? ->
            if (itemStacks == null) {
                return@modify null
            }
            itemStacks.forEach(Consumer { item: ItemStack ->
                Display.display(
                    item, player
                )
            })
            itemStacks
        }
    }
}