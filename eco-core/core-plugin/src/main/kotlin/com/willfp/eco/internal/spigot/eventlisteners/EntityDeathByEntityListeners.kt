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
    private val creditIndirect = plugin.configYml.getBool("kill-attribution.credit-indirect-kills")

    private val windowTicks = if (creditIndirect) {
        plugin.configYml.getInt("kill-attribution.window-ticks").toLong()
    } else {
        LEGACY_WINDOW_TICKS
    }

    private val tracker = LastDamagerTracker(windowTicks) { Bukkit.getCurrentTick().toLong() }

    init {
        plugin.scheduler.global().runTimer(windowTicks.coerceAtLeast(1), windowTicks.coerceAtLeast(1)) {
            tracker.purgeExpired()
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? LivingEntity ?: return

        if (!creditIndirect && victim.health > event.finalDamage) {
            return
        }

        tracker.record(victim.uniqueId, event.damager)
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

    private companion object {
        // The window eco used before indirect kills were creditable, kept to
        // stay in step with WildStacker.
        const val LEGACY_WINDOW_TICKS = 5L
    }
}
