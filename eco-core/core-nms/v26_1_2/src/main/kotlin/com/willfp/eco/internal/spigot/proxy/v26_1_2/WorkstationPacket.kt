package com.willfp.eco.internal.spigot.proxy.v26_1_2

import com.willfp.eco.core.packet.Packet
import com.willfp.eco.internal.spigot.proxies.WorkstationPacketProxy
import com.willfp.eco.internal.spigot.proxy.common.toNMS
import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import org.bukkit.entity.Player

class WorkstationPacket : WorkstationPacketProxy {
    override fun getContainerClickSlot(packet: Packet): Int? =
        (packet.handle as? ServerboundContainerClickPacket)?.slotNum?.toInt()

    override fun getOpenContainerId(player: Player): Int =
        player.toNMS().containerMenu.containerId

    override fun sendContainerDataPacket(player: Player, containerId: Int, value: Int) {
        player.toNMS().connection.send(ClientboundContainerSetDataPacket(containerId, 0, value))
    }
}
