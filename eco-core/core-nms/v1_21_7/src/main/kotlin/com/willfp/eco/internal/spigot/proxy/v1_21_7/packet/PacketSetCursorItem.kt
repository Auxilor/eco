package com.willfp.eco.internal.spigot.proxy.v1_21_7.packet

import com.google.common.cache.LoadingCache
import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.DisplayFrame
import com.willfp.eco.internal.spigot.proxy.common.packet.display.frame.lastDisplayFrame
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.network.HashedStack
import net.minecraft.network.protocol.game.ClientboundSetCursorItemPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.inventory.ContainerSynchronizer
import org.bukkit.craftbukkit.entity.CraftPlayer
import java.util.Objects


fun HashedStack.ActualItem.hash(): Int {
    val item = this.item
    val count = this.count
    val components = this.components

    // Sort to ensure a consistent hash
    val sortedAddedComponents = components.addedComponents.entries
        .sortedBy { it.key.toString() }
    val sortedRemovedComponents = components.removedComponents
        .sortedBy { it.toString() }

    return Objects.hash(item, count, sortedAddedComponents, sortedRemovedComponents)
}

object PacketSetCursorItem : PacketListener {
    private val containerSynchronizerField = ServerPlayer::class.java
        .declaredFields
        .first { it.type == ContainerSynchronizer::class.java }
        .apply { isAccessible = true }

    private fun ContainerSynchronizer.getCache(): LoadingCache<TypedDataComponent<*>, Int> {
        @Suppress("UNCHECKED_CAST")
        return this::class.java
            .declaredFields
            .first { it.type == LoadingCache::class.java }
            .apply { isAccessible = true }
            .get(this) as LoadingCache<TypedDataComponent<*>, Int>
    }

    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundSetCursorItemPacket ?: return

        val contents = packet.contents

        if (contents.isEmpty) {
            return
        }

        val item = contents.asBukkitStack()
        val player = event.player
        val serverPlayer = (player as CraftPlayer).handle

        val containerSynchronizer = containerSynchronizerField.get(serverPlayer) as ContainerSynchronizer
        val cache = containerSynchronizer.getCache()

        val original = HashedStack.create(packet.contents, cache::getUnchecked)

        Display.display(item, player)

        val displayed = HashedStack.create(packet.contents, cache::getUnchecked)

        PacketContainerClick.map(original, (displayed as HashedStack.ActualItem).hash())

        player.lastDisplayFrame = DisplayFrame.EMPTY
    }
}
