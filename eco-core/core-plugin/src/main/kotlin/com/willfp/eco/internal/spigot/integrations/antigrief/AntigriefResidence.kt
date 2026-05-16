package com.willfp.eco.internal.spigot.integrations.antigrief

import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.containers.Flags
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefResidence : AntigriefIntegration {
    override fun canBreakBlock(player: Player, block: Block): Boolean {
        val residence = ResidenceApi.getResidenceManager().getByLoc(block.location) ?: return true
        return residence.permissions.playerHas(player, Flags.destroy, false)
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        val residence = ResidenceApi.getResidenceManager().getByLoc(block.location) ?: return true
        return residence.permissions.playerHas(player, Flags.place, false)
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        val residence = ResidenceApi.getResidenceManager().getByLoc(location) ?: return true
        return residence.permissions.has(Flags.explode, false)
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        val residence = ResidenceApi.getResidenceManager().getByLoc(victim.location) ?: return true
        return if (victim is Player) {
            residence.permissions.has(Flags.pvp, false)
        } else {
            residence.permissions.playerHas(player, Flags.mobkilling, false)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val residence = ResidenceApi.getResidenceManager().getByLoc(location) ?: return true
        return residence.permissions.playerHas(player, Flags.itempickup, true)
    }

    override fun getPluginName(): String {
        return "Residence"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefIntegration) return false
        return other.pluginName == this.pluginName
    }

    override fun hashCode(): Int {
        return this.pluginName.hashCode()
    }
}