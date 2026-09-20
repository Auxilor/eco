package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.Eco
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.cache.EcoCache
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDeathEvent
import java.time.Duration
import java.util.UUID

class EntityDeathByEntityListeners(
    private val plugin: EcoPlugin
) : Listener {
    private val creditIndirect = plugin.configYml.getBool("kill-attribution.credit-indirect-kills")

    private val windowTicks = if (creditIndirect) {
        plugin.configYml.getInt("kill-attribution.window-ticks").toLong()
    } else {
        LEGACY_WINDOW_TICKS
    }

    private val lastDamagers: EcoCache<UUID, UUID> = EcoCache.builder<UUID, UUID>()
        .expireAfterWrite(Duration.ofMillis(windowTicks.coerceAtLeast(1) * MILLIS_PER_TICK))
        .build()

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? LivingEntity ?: return

        if (!creditIndirect && victim.health > event.finalDamage) {
            return
        }

        lastDamagers.put(victim.uniqueId, event.damager.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDeath(event: EntityDeathEvent) {
        val victim = event.entity
        val damagerId = lastDamagers.get(victim.uniqueId) ?: return

        lastDamagers.invalidate(victim.uniqueId)

        val damager = resolveDamager(victim, damagerId) ?: return

        val builtEvent = EntityDeathByEntityBuilder()
        builtEvent.victim = victim
        builtEvent.damager = damager
        builtEvent.drops = event.drops
        builtEvent.xp = event.droppedExp
        builtEvent.deathEvent = event

        builtEvent.push()
    }

    private fun resolveDamager(victim: LivingEntity, damagerId: UUID): Entity? {
        val damager = victim.world.getEntity(damagerId) ?: return null

        return if (Eco.get().isOwnedByCurrentRegion(damager)) damager else null
    }

    private companion object {
        const val LEGACY_WINDOW_TICKS = 5L

        const val MILLIS_PER_TICK = 50L
    }
}
