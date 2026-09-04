package com.willfp.eco.internal.spigot.integrations.antigrief

import com.bekvon.bukkit.residence.containers.Flags
import com.bekvon.bukkit.residence.listeners.ResidenceBlockListener
import com.bekvon.bukkit.residence.listeners.ResidenceEntityListener
import com.bekvon.bukkit.residence.protection.FlagPermissions
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefResidence : AntigriefIntegration {
    override fun canBreakBlock(player: Player, block: Block): Boolean {
        return ResidenceBlockListener.canBreakBlock(player, block, false)
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        return ResidenceBlockListener.canPlaceBlock(player, block, false)
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        return FlagPermissions.getPerms(location, player)
            .has(Flags.explode, FlagPermissions.FlagCombo.TrueOrNone)
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        return ResidenceEntityListener.canDamageEntity(player, victim, false)
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return FlagPermissions.getPerms(location, player)
            .playerHas(player, Flags.itempickup, FlagPermissions.FlagCombo.TrueOrNone)
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
