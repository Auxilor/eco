package com.willfp.eco.spigot.eventlisteners

import com.willfp.eco.core.events.PlayerJumpEvent
import com.willfp.eco.core.integrations.mcmmo.McmmoManager
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.potion.PotionEffectType
import java.text.DecimalFormat
import java.util.*

@Suppress("DEPRECATION")
class PlayerJumpListeners : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onJump(event: PlayerMoveEvent) {
        if (McmmoManager.isFake(event)) {
            return
        }
        val player = event.player
        if (player.velocity.y > 0) {
            var jumpVelocity = 0.42f
            if (player.hasPotionEffect(PotionEffectType.JUMP)) {
                jumpVelocity += (player.getPotionEffect(PotionEffectType.JUMP)!!.amplifier.toFloat() + 1) * 0.1f
            }
            jumpVelocity = FORMAT.format(jumpVelocity.toDouble()).replace(',', '.').toFloat()
            if (event.player.location.block.type != Material.LADDER && PREVIOUS_PLAYERS_ON_GROUND.contains(player.uniqueId)
                && !player.isOnGround
                && java.lang.Float.compare(player.velocity.y.toFloat(), jumpVelocity) == 0
            ) {
                Bukkit.getPluginManager().callEvent(PlayerJumpEvent(event))
            }
        }
        if (player.isOnGround) {
            PREVIOUS_PLAYERS_ON_GROUND.add(player.uniqueId)
        } else {
            PREVIOUS_PLAYERS_ON_GROUND.remove(player.uniqueId)
        }
    }

    companion object {
        private val PREVIOUS_PLAYERS_ON_GROUND: MutableSet<UUID> = HashSet()
        private val FORMAT = DecimalFormat("0.00")
    }
}