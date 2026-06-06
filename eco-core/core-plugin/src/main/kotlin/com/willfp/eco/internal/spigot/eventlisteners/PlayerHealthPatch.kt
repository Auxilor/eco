package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.Eco
import com.willfp.eco.util.saveHealth
import com.willfp.eco.util.savedHealth
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.math.min

object PlayerHealthPatch: Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun handlePlayerQuit(event: PlayerQuitEvent) {
        event.player.saveHealth()
	}

    @EventHandler(priority = EventPriority.MONITOR)
    fun handlePlayerJoin(event: PlayerJoinEvent) {
		if (Eco.get().ecoPlugin.configYml.getBool("enable-health-fix")) {
			// Start a short repeating task to detect attribute changes as soon as they are applied.
			// Keep it active for a fixed window so multiple delayed changes to MAX_HEALTH attribute can be handled.
			val initialMax = event.player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
			val previousMax = initialMax

			// Run every tick for up to 100 ticks (~5s).
			var repeatingTask: org.bukkit.scheduler.BukkitTask? = null
			var ticksRan = 0
			repeatingTask = Eco.get().ecoPlugin.scheduler.runTimer({
				try {
					ticksRan++
					// 100 ticks = 5 sec
					if (ticksRan >= 100) {
						repeatingTask?.cancel()
						return@runTimer
					}

					if (!event.player.isOnline || event.player.isDead) return@runTimer

					val currentMax = event.player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
					if (currentMax != previousMax) {
						// Max health changed.
						val oldMax = previousMax
						val currentHealth = event.player.health

						val newHealth = if (oldMax <= 0.0) {
							// Fallback: set to current max or saved health
							min(event.player.savedHealth, currentMax)
						} else {
							// If player was at (or very near) full health before the change, top them up to the new max.
							if (currentHealth >= oldMax - 0.0001) {
								min(event.player.savedHealth, currentMax)
							} else {
								// Otherwise, preserve the same percentage of health.
								val pct = currentHealth / oldMax
								min(currentMax * pct, currentMax)
							}
						}

						event.player.health = newHealth
					}
				} catch (ex: Exception) {
					Eco.get().ecoPlugin.logger.warning("[HEALTH-FIX] Exception while monitoring health attribute: ${ex.message}")
				}
			}, 1L, 1L)
		}
	}
}