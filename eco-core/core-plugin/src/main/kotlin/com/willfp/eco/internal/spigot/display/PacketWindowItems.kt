package com.willfp.eco.internal.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.HashedItem
import com.willfp.eco.internal.spigot.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.display.frame.lastDisplayFrame
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

        /*When things are working well, windowId is non-zero
        * but occasionally this onsend method is called
        * more than once with the additional calls having
        * a windowId of 0. Those additional calls didn't
        * allow the DisplayFrame to be set to empty and
        * caused the display to break for tiers and enchants
        * -Bradarr */

        //if (windowId != 0) {
            player.lastDisplayFrame = DisplayFrame.EMPTY
        //}

        val itemStacks = packet.itemListModifier.read(0) ?: return

        packet.itemListModifier.write(0, modifyWindowItems(itemStacks, windowId, player))
    }

    private fun modifyWindowItems(
        itemStacks: MutableList<ItemStack>,
        windowId: Int,
        player: Player
    ): MutableList<ItemStack> {
        if (this.getPlugin().configYml.getBool("use-display-frame") && windowId == 0) {
            val frameMap = mutableMapOf<Byte, HashedItem>()

            for (index in itemStacks.indices) {
                frameMap[index.toByte()] = HashedItem.of(itemStacks[index])
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
