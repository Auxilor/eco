package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.internal.display.EcoDisplayHandler
import com.willfp.eco.spigot.display.frame.DisplayFrame
import com.willfp.eco.spigot.display.frame.HashedItem
import com.willfp.eco.spigot.display.frame.lastDisplayFrame
import org.bukkit.entity.Player
import java.util.concurrent.ConcurrentHashMap

class PacketWindowItems(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, false) {
    private val ignorePacketList = ConcurrentHashMap.newKeySet<String>()

    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        if (ignorePacketList.contains(player.name)) {
            ignorePacketList.remove(player.name)
            return
        }

        val windowId = packet.integers.read(0)

        if (windowId != 0) {
            player.lastDisplayFrame = DisplayFrame.EMPTY
        }

        val itemStacks = packet.itemListModifier.read(0) ?: return

        val displayTask = {
            if (this.getPlugin().configYml.getBool("use-display-frame") && windowId == 0) {
                val frameMap = mutableMapOf<Byte, HashedItem>()

                for (index in itemStacks.indices) {
                    frameMap[index.toByte()] =
                        HashedItem(FastItemStack.wrap(itemStacks[index]).hashCode(), itemStacks[index])
                }

                val newFrame = DisplayFrame(frameMap)

                val lastFrame = player.lastDisplayFrame

                player.lastDisplayFrame = newFrame

                val changes = lastFrame.getChangedSlots(newFrame)

                for (index in changes) {
                    Display.display(itemStacks[index.toInt()], player)
                }

                for (index in (itemStacks.indices subtract changes.toSet())) {
                    itemStacks[index.toInt()] = lastFrame.getItem(index.toByte()) ?: itemStacks[index.toInt()]
                }
            } else {
                itemStacks.forEach { Display.display(it, player) }
            }

            val newPacket = packet.deepClone()
            newPacket.itemListModifier.write(0, itemStacks)

            ignorePacketList.add(player.name)

            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket)
        }

        (Display.getHandler() as EcoDisplayHandler).executor.execute(displayTask)
    }
}