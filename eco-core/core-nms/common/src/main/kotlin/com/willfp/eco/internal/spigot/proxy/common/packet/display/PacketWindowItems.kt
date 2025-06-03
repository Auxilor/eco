package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.items.HashedItem
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

open class PacketWindowItems(
    private val plugin: EcoPlugin
) : PacketListener {
    private val lastKnownWindowIDs = ConcurrentHashMap<UUID, Int>()

    private val field = ClientboundContainerSetContentPacket::class.java
        .declaredFields
        .first { it.type == List::class.java }
        .apply { isAccessible = true }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundContainerSetContentPacket ?: return
        val player = event.player

        Display.display(packet.carriedItem.asBukkitStack(), player)

        val windowId = packet.containerId

        val lastKnownID = lastKnownWindowIDs[player.uniqueId]
        lastKnownWindowIDs[player.uniqueId] = windowId

        // If there is any change in window ID at any point,
        // Remove the last display frame to prevent any potential conflicts.
        // If the window ID is not zero (not a player inventory), then remove too,
        // as GUIs are not player inventories.
        if (lastKnownID != windowId || windowId != 0) {
            player.lastDisplayFrame = DisplayFrame.EMPTY
        }

        val itemStacks = packet.items.map { it.asBukkitStack() }

        val newItems = modifyWindowItems(itemStacks.toMutableList(), windowId, player)

        field.set(packet, newItems.map { it.asNMSStack() })
    }


    protected fun modifyWindowItems(
        itemStacks: MutableList<ItemStack>,
        windowId: Int,
        player: Player
    ): MutableList<ItemStack> {
        if (plugin.configYml.getBool("use-display-frame") && windowId == 0) {
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
