package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.EcoPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent

class EntityDeathByEntityListeners(
    private val plugin: EcoPlugin
) : Listener {
    private val windowTicks = plugin.configYml.getInt("kill-attribution.window-ticks").toLong()

    private val tracker = LastDamagerTracker(windowTicks) { Bukkit.getCurrentTick().toLong() }

    init {
        plugin.scheduler.runTimer(windowTicks.coerceAtLeast(1), windowTicks.coerceAtLeast(1)) {
            tracker.purgeExpired()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (event.entity !is LivingEntity) {
            return
        }

        tracker.record(event.entity.uniqueId, event.damager)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDeath(event: EntityDeathEvent) {
        val victim = event.entity
        val damager = tracker.resolve(victim.uniqueId) ?: return

        tracker.forget(victim.uniqueId)

        val builtEvent = EntityDeathByEntityBuilder()
        builtEvent.victim = victim
        builtEvent.damager = damager
        builtEvent.drops = event.drops
        builtEvent.xp = event.droppedExp
        builtEvent.deathEvent = event

        builtEvent.push()
    }
}
