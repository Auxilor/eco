package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.proxy.AutoCraftProxy
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

class PacketAutoRecipe(plugin: EcoPlugin) : AbstractPacketAdapter(plugin, PacketType.Play.Server.AUTO_RECIPE, false) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        if (!EcoPlugin.getPluginNames()
                .contains(packet.minecraftKeys.values[0].fullKey.split(":".toRegex()).toTypedArray()[0])
        ) {
            return
        }
        if (packet.minecraftKeys.values[0].fullKey.split(":".toRegex()).toTypedArray()[1].contains("displayed")) {
            return
        }
        getPlugin().getProxy(AutoCraftProxy::class.java).modifyPacket(packet.handle)
        val newAutoRecipe = PacketContainer(PacketType.Play.Server.AUTO_RECIPE)
        newAutoRecipe.minecraftKeys.write(0, packet.minecraftKeys.read(0))
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, newAutoRecipe)
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
    }
}