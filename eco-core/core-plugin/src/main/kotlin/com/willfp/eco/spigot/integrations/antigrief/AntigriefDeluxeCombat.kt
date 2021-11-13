package com.willfp.eco.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import nl.marido.deluxecombat.api.DeluxeCombatAPI
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefDeluxeCombat: AntigriefWrapper {
    override fun getPluginName(): String {
        return "DeluxeCombat"
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
        val api = DeluxeCombatAPI()
        return when(victim) {
            is Player -> ((!api.hasProtection(victim) && api.hasPvPEnabled(victim)) || api.isInCombat(victim))
            else -> true
        }
    }
}