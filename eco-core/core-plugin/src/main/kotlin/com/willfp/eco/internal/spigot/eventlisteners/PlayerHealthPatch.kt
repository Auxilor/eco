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
import org.bukkit.scheduler.BukkitTask
import kotlin.math.min

object PlayerHealthPatch: Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun handlePlayerQuit(event: PlayerQuitEvent) {
        event.player.saveHealth()
	}

    @EventHandler(priority = EventPriority.MONITOR)
    fun handlePlayerJoin(event: PlayerJoinEvent) {
		if (Eco.get().ecoPlugin.configYml.getBool("enable-health-fix")) {
			var fixDuration = Eco.get().ecoPlugin.configYml.getInt("health-fix-duration", 3)
			if (fixDuration <= 0) fixDuration = 3

			var fixInterval = Eco.get().ecoPlugin.configYml.getInt("health-fix-interval", 1)
			if (fixInterval <= 0) fixInterval = 1
			else if (fixInterval > fixDuration) fixInterval = fixDuration

			// ceil fix for int
			var timesToRun = (fixDuration + fixInterval - 1) / fixInterval

			var previousMax = event.player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

			// Run every tick for up to user defined duration in config.
			var repeatingTask: BukkitTask? = null
			repeatingTask = Eco.get().ecoPlugin.scheduler.runTimer({
				try {
					if (timesToRun <= 0) {
						repeatingTask?.cancel()
						return@runTimer
					}

					if (!event.player.isOnline || event.player.isDead) return@runTimer

					val currentMax = event.player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
					if (currentMax != previousMax) {
						// Max health changed.
						val currentHealth = event.player.health
						val savedHealth = event.player.savedHealth
						// Get new health based on logic checks
						val newHealth = getNewHealth(currentHealth, savedHealth, currentMax, previousMax)

						event.player.health = newHealth

						previousMax = currentMax
					}
				} catch (ex: Exception) {
					Eco.get().ecoPlugin.logger.warning("Exception while monitoring health attribute: ${ex.message}")
				} finally {
					timesToRun--;
				}

			}, 1L, fixInterval * 20L)
		}
	}

	fun getNewHealth(currentHealth : Double,
					 savedHealth : Double,
					 currentMax : Double,
					 previousMax : Double
					): Double {

		if (previousMax <= 0.0) {
			// Fallback: set to current max or saved health
			return min(savedHealth, currentMax)
		}

		if (currentHealth >= previousMax) {
			return min(savedHealth, currentMax)
		} else {
			// Otherwise, preserve the same percentage of health.
			val percent = currentHealth / previousMax
			return min(currentMax * percent, currentMax)
		}
	}
}