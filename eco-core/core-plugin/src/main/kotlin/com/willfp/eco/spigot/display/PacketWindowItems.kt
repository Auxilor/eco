package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.spigot.display.frame.DisplayFrame
import com.willfp.eco.spigot.display.frame.HashedItem
import com.willfp.eco.spigot.display.frame.lastDisplayFrame
import com.willfp.eco.util.ServerUtils
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PacketWindowItems(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.WINDOW_ITEMS, false) {
    private val ignorePacketList = ConcurrentHashMap.newKeySet<String>()
    private val executor: ExecutorService = Executors.newCachedThreadPool(
        ThreadFactoryBuilder().setNameFormat("eco-display-thread-%d").build()
    )

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

        if (usingAsync()) {
            executor.execute {
                try {
                    modifyWindowItems(itemStacks, windowId, player)
                } catch (e: Exception) {
                    // Don't do anything, just accept defeat
                }

                val newPacket = packet.deepClone()
                newPacket.itemListModifier.write(0, itemStacks)

                ignorePacketList.add(player.name)

                ProtocolLibrary.getProtocolManager().sendServerPacket(player, newPacket)
            }
        } else {
            packet.itemListModifier.write(0, modifyWindowItems(itemStacks, windowId, player))
        }
    }

    private fun usingAsync(): Boolean {
        if (this.getPlugin().configYml.getBool("use-async.display")) {
            return true
        }

        if (this.getPlugin().configYml.getBool("use-emergency-async-display") && ServerUtils.getTps() < 18) {
            return true
        }

        return false
    }

    private fun modifyWindowItems(
        itemStacks: MutableList<ItemStack>,
        windowId: Int,
        player: Player
    ): MutableList<ItemStack> {
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

        return itemStacks
    }
}