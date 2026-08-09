package com.willfp.eco.internal.spigot.eventlisteners.armor

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import com.willfp.eco.core.events.ArmorEquipEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * Paper fires an event for every armor slot mutation, so it catches the changes that the
 * heuristics in [ArmorListener] miss, such as swapping out a piece that's already worn.
 */
class ArmorListenerPaper : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onArmorChange(event: PlayerArmorChangeEvent) {
        Bukkit.getPluginManager().callEvent(ArmorEquipEvent(event.player))
    }
}
