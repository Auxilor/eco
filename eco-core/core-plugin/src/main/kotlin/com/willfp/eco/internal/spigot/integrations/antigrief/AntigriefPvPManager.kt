package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import me.NoChance.PvPManager.PvPlayer
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefPvPManager: AntigriefIntegration {
    override fun getPluginName(): String {
        return "PvPManager"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        return true
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        return true
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        return true
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        return when(victim) {
            is Player -> {
                val defender = PvPlayer.get(victim)
                (defender.hasPvPEnabled() && !defender.isNewbie || defender.isInCombat)}
            else -> true
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }
}
