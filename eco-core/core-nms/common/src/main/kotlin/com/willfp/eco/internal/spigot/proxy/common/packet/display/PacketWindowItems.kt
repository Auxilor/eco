package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.fast.FastItemStack
import com.willfp.eco.core.items.HashedItem
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
            val lastFrame = player.lastDisplayFrame

            // Hashes of the items as they arrived, before display, so that the next frame can
            // tell whether the server-side item changed.
            val hashes = itemStacks.map { FastItemStack.wrap(it).hashCode() }

            for (index in itemStacks.indices) {
                if (lastFrame.getHash(index.toByte()) == hashes[index]) {
                    // Unchanged since the last frame, so reuse what was sent last time rather
                    // than displaying again.
                    itemStacks[index] = lastFrame.getItem(index.toByte()) ?: itemStacks[index]
                } else {
                    Display.display(itemStacks[index], player)
                }
            }

            // The frame caches the items that were actually sent, keyed by the hash of the
            // items they were made from. Caching the pre-display items instead would send them
            // out undisplayed the next time the slot was unchanged.
            val frameMap = mutableMapOf<Byte, HashedItem>()

            for (index in itemStacks.indices) {
                frameMap[index.toByte()] = HashedItem.of(itemStacks[index], hashes[index])
            }

            player.lastDisplayFrame = DisplayFrame(frameMap)
        } else {
            itemStacks.forEach { Display.display(it, player) }
        }

        return itemStacks
    }
}
