package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.spigot.display.frame.DisplayFrame
import com.willfp.eco.spigot.display.frame.lastDisplayFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PacketWindowItems(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, false) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        val windowId = packet.integers.read(0)

        packet.itemListModifier.modify(0) { itemStacks: List<ItemStack>? ->
            if (itemStacks == null) {
                return@modify null
            }

            if (windowId == 0) {
                val frameMap = mutableMapOf<Byte, Int>()

                for (index in itemStacks.indices) {
                    frameMap[index.toByte()] = FastItemStack.wrap(itemStacks[index]).hashCode()
                }

                val newFrame = DisplayFrame(frameMap)

                val changes = player.lastDisplayFrame.getChangedSlots(newFrame)

                player.lastDisplayFrame = newFrame

                for (index in changes) {
                    Display.display(itemStacks[index.toInt()], player)
                }
            } else {
                itemStacks.forEach {
                    Display.display(it, player)
                }
            }
            itemStacks
        }
    }
}