package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

object EntityDeathByEntityListener: Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDeath(event: EntityDeathEvent) {
        val damageEvent = event.entity.lastDamageCause as? EntityDamageByEntityEvent ?: return
        val killer = damageEvent.damager

        Bukkit.getPluginManager().callEvent(
            EntityDeathByEntityEvent(
                event.entity,
                killer,
                event.drops,
                event.droppedExp,
                event
            )
        )
    }
}
