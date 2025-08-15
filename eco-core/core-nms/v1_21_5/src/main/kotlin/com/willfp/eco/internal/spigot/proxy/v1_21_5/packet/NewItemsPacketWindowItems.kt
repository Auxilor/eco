package com.willfp.eco.internal.spigot.proxy.v1_21_5.packet

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.asNMSStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.PacketWindowItems
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class NewItemsPacketWindowItems(
    plugin: EcoPlugin
) : PacketWindowItems(plugin) {
    private val lastKnownWindowIDs = ConcurrentHashMap<UUID, Int>()

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

        val newPacket = ClientboundContainerSetContentPacket(
            packet.containerId,
            packet.stateId,
            newItems.map { it.asNMSStack() },
            packet.carriedItem,
        )

        event.packet.handle = newPacket
    }
}
