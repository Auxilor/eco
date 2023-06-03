package com.willfp.eco.internal.events

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.EventManager
import com.willfp.eco.core.packet.PacketEvent
import com.willfp.eco.core.packet.PacketListener
import com.willfp.eco.core.packet.PacketPriority
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener


private class RegisteredPacketListener(
    val plugin: EcoPlugin,
    val listener: PacketListener
)

private val listeners = mutableMapOf<PacketPriority, MutableList<RegisteredPacketListener>>()

fun PacketEvent.handleSend() {
    for (priority in PacketPriority.values()) {
        for (listener in listeners[priority] ?: continue) {
            try {
                listener.listener.onSend(this)
            } catch (e: Exception) {
                listener.plugin.logger.warning(
                    "Exception in packet listener ${listener.listener.javaClass.simpleName}" +
                            " for packet ${packet.javaClass.simpleName}!"
                )
                e.printStackTrace()
            }
        }
    }
}

fun PacketEvent.handleReceive() {
    for (priority in PacketPriority.values()) {
        for (listener in listeners[priority] ?: continue) {
            try {
                listener.listener.onReceive(this)
            } catch (e: Exception) {
                listener.plugin.logger.warning(
                    "Exception in packet listener ${listener.listener.javaClass.simpleName}" +
                            " for packet ${packet.javaClass.simpleName}!"
                )
                e.printStackTrace()
            }
        }
    }
}

class EcoEventManager(private val plugin: EcoPlugin) : EventManager {
    override fun registerListener(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }

    override fun unregisterListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }

    override fun unregisterAllListeners() {
        HandlerList.unregisterAll(plugin)
        for (value in listeners.values) {
            value.removeIf { it.plugin == plugin }
        }
    }

    override fun registerPacketListener(listener: PacketListener) {
        listeners.getOrPut(listener.priority) { mutableListOf() }.add(
            RegisteredPacketListener(
                plugin,
                listener
            )
        )
    }
}
