package com.willfp.eco.internal.spigot.display

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.AdventureComponentConverter
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.willfp.eco.core.AbstractPacketAdapter
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.internal.spigot.proxy.ChatComponentProxy
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.chat.ComponentSerializer
import org.bukkit.entity.Player

class PacketChat(plugin: EcoPlugin) :
    AbstractPacketAdapter(plugin,
        PacketType.Play.Server.CHAT,
        if (plugin.configYml.getBool("use-lower-protocollib-priority")) ListenerPriority.NORMAL else ListenerPriority.HIGHEST,
        true) {
    override fun onSend(
        packet: PacketContainer,
        player: Player,
        event: PacketEvent
    ) {
        val component = packet.modifier.read(1)
        var newComponent: Any? = null
        var currentWrappedComponent: WrappedChatComponent? = null
        if (component is Array<*> && component.isArrayOf<BaseComponent>()) {
            currentWrappedComponent = WrappedChatComponent.fromJson(ComponentSerializer.toString(component))
            val newWrappedComponent = WrappedChatComponent.fromHandle(
                getPlugin().getProxy(
                    ChatComponentProxy::class.java
                ).modifyComponent(currentWrappedComponent.handle, player)
            )
            newComponent = ComponentSerializer.parse(newWrappedComponent.json)
        } else if (Prerequisite.HAS_PAPER.isMet && component is Component) {
            currentWrappedComponent = AdventureComponentConverter.fromComponent(component)
            val newWrappedComponent = WrappedChatComponent.fromHandle(
                getPlugin().getProxy(
                    ChatComponentProxy::class.java
                ).modifyComponent(currentWrappedComponent.handle, player)
            )
            newComponent = AdventureComponentConverter.fromWrapper(newWrappedComponent)
        }
        if (currentWrappedComponent == null) {
            return
        }
        if (newComponent != null) {
            packet.modifier.write(1, newComponent)
        }
    }
}