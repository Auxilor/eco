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
			val fixDuration = Eco.get().ecoPlugin.configYml.getInt("health-fix-duration", 3)

			val previousMax = event.player.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0

			// Run every tick for up to user defined duration in config.
			var repeatingTask: BukkitTask? = null
			var ticksRan = 0
			repeatingTask = Eco.get().ecoPlugin.scheduler.runTimer({
				try {
					ticksRan++

					// 3 sec = 60 tick (as 1 sec = 20 tick)
					if (ticksRan >= fixDuration * 20) {
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
					}
				} catch (ex: Exception) {
					Eco.get().ecoPlugin.logger.warning("Exception while monitoring health attribute: ${ex.message}")
				}
			}, 1L, 1L)
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