package com.willfp.eco.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import proxy.ChatComponentProxy
import org.bukkit.entity.Player

class PacketChat(plugin: EcoPlugin) :
    AbstractPacketAdapter(plugin, PacketType.Play.Server.CHAT, ListenerPriority.MONITOR, true) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        for (i in 0 until packet.chatComponents.size()) {
            val component = packet.chatComponents.read(i) ?: continue
            if (component.handle == null) {
                return
            }
            val newComponent = WrappedChatComponent.fromHandle(
                getPlugin().getProxy(
                    ChatComponentProxy::class.java
                ).modifyComponent(component.handle, player)
            )
            packet.chatComponents.write(i, newComponent)
        }
    }
}