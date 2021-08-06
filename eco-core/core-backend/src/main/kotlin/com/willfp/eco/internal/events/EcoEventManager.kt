package com.willfp.eco.internal.events

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.events.EventManager
import org.bukkit.Bukkit
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class EcoEventManager constructor(plugin: EcoPlugin) : PluginDependent<EcoPlugin>(plugin), EventManager {
    override fun registerListener(listener: Listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin)
    }

    override fun unregisterListener(listener: Listener) {
        HandlerList.unregisterAll(listener)
    }

    override fun unregisterAllListeners() {
        HandlerList.unregisterAll(plugin)
    }
}