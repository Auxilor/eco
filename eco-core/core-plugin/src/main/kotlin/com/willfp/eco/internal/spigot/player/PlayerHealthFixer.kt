package com.willfp.eco.internal.spigot.player

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerHealthFixer(
    private val plugin: EcoPlugin
): Listener {
    private val lastHealthKey = PersistentDataKey(
        plugin.createNamespacedKey("last_health"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        if (!plugin.configYml.getBool("health-fixer")) {
            return
        }

        val player = event.player
        player.profile.write(lastHealthKey, player.health)
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        if (!plugin.configYml.getBool("health-fixer")) {
            return
        }

        val player = event.player
        plugin.scheduler.runLater(2) {
            player.health = player.profile.read(lastHealthKey)
        }
    }
}
