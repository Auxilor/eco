package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.EcoPlugin
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathByEntityListeners(
    private val plugin: EcoPlugin
) : Listener {
    private val events = mutableSetOf<EntityDeathByEntityBuilder>()

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if ((event.entity !is LivingEntity)) {
            return
        }

        val victim = event.entity as LivingEntity

        if (victim.health > event.finalDamage) {
            return
        }

        val builtEvent = EntityDeathByEntityBuilder()
        builtEvent.victim = victim
        builtEvent.damager = event.damager

        events += builtEvent

        this.plugin.scheduler.runTaskLater(5) { // Fixes conflicts with WildStacker
            events.remove(builtEvent)
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDeath(event: EntityDeathEvent) {
        val victim = event.entity
        val drops = event.drops
        val xp = event.droppedExp

        var builtEvent: EntityDeathByEntityBuilder? = null

        for (builder in events) {
            if (builder.victim == victim) {
                builtEvent = builder
            }
        }

        if (builtEvent == null) {
            return
        }

        events.remove(builtEvent)
        builtEvent.drops = drops
        builtEvent.xp = xp
        builtEvent.deathEvent = event

        builtEvent.push()
    }
}
