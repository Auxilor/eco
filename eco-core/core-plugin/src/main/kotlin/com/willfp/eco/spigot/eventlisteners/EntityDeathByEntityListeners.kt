package com.willfp.eco.spigot.eventlisteners

import com.willfp.eco.core.EcoPlugin
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import java.util.concurrent.atomic.AtomicReference

class EntityDeathByEntityListeners(
    private val plugin: EcoPlugin
) : Listener {
    private val events = HashSet<EntityDeathByEntityBuilder>()

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
        events.add(builtEvent)
        this.plugin.scheduler.runLater({
            events.remove(builtEvent)
        }, 1)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val victim = event.entity
        val drops = event.drops
        val xp = event.droppedExp

        val atomicBuiltEvent = AtomicReference<EntityDeathByEntityBuilder>(null)

        for (builder in events) {
            if (builder.victim == victim) {
                atomicBuiltEvent.set(builder)
            }
        }

        if (atomicBuiltEvent.get() == null) {
            return
        }

        val builtEvent = atomicBuiltEvent.get()
        events.remove(builtEvent)
        builtEvent.drops = drops
        builtEvent.xp = xp
        builtEvent.deathEvent = event

        builtEvent.push()
    }
}
