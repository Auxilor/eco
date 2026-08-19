package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.world.item.ItemStack

/**
 * Items that live on an entity rather than in a container are synced through
 * entity data - dropped items and item frames - so they never pass through the
 * container packets and would otherwise be shown to the client undisplayed.
 */
object PacketSetEntityData : PacketListener {
    override fun onSend(event: PacketEvent) {
        val packet = event.packet.handle as? ClientboundSetEntityDataPacket ?: return

        for (value in packet.packedItems()) {
            val item = value.value() as? ItemStack ?: continue

            if (item.isEmpty) {
                continue
            }

            Display.display(item.asBukkitStack(), event.player)
        }
    }
}
