package com.willfp.eco.internal.spigot.integrations.customitems.mythicmobs

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicDropLoadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class MythicDropListener: Listener {
    @EventHandler
    fun onLoad(event: MythicDropLoadEvent) {
        val name = event.dropName
        if (name.equals("Eco", true)) {
            event.register(
                CustomItemsMythicMobs(event.config)
            )
        }
    }
}