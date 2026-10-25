package com.willfp.eco.internal.spigot.proxy.common.packet.display

import com.willfp.eco.core.display.Display
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.internal.spigot.proxy.common.asBukkitStack
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.item.ItemStack
import org.bukkit.entity.Player

object PacketSetEntityData : PacketListener {
    override fun onSend(event: PacketEvent) {
        val packet = event.packet
        val player = event.player

        when (val handle = packet.handle) {
            is ClientboundSetEntityDataPacket -> handle.displayed(player)?.let { packet.handle = it }
            is ClientboundBundlePacket -> handle.displayed(player)?.let { packet.handle = it }
        }
    }

    private fun ClientboundBundlePacket.displayed(player: Player): ClientboundBundlePacket? {
        var changed = false

        val subPackets = this.subPackets().map { subPacket ->
            (subPacket as? ClientboundSetEntityDataPacket)?.displayed(player)?.also { changed = true } ?: subPacket
        }

        @Suppress("UNCHECKED_CAST")
        return if (changed) ClientboundBundlePacket(subPackets as List<Packet<in ClientGamePacketListener>>) else null
    }

    /**
     * The same packet object is broadcast to every viewer, so display goes on copies in a new packet.
     */
    private fun ClientboundSetEntityDataPacket.displayed(player: Player): ClientboundSetEntityDataPacket? {
        if (this.packedItems.none { it.serializer == EntityDataSerializers.ITEM_STACK }) {
            return null
        }

        return ClientboundSetEntityDataPacket(this.id, this.packedItems.map { it.displayed(player) })
    }

    private fun SynchedEntityData.DataValue<*>.displayed(player: Player): SynchedEntityData.DataValue<*> {
        if (this.serializer != EntityDataSerializers.ITEM_STACK) {
            return this
        }

        val itemStack = (this.value as ItemStack).takeUnless { it.isEmpty }?.copy() ?: return this

        Display.display(itemStack.asBukkitStack(), player)

        return SynchedEntityData.DataValue(this.id, EntityDataSerializers.ITEM_STACK, itemStack)
    }
}
