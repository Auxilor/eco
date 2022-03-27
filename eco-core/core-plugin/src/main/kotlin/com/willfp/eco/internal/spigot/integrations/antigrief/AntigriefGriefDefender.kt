package com.willfp.eco.internal.spigot.integrations.antigrief

import com.griefdefender.api.GriefDefender
import com.ssomar.score.usedapi.GriefDefenderAPI
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import me.ryanhamshire.GriefPrevention.Claim
import me.ryanhamshire.GriefPrevention.GriefPrevention
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefGriefDefender : AntigriefWrapper {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val user = GriefDefender.getCore().getUser(player.uniqueId)?: return true
        return user.canBreak(block.location)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val user = GriefDefender.getCore().getUser(player.uniqueId)?: return true
        return user.canBreak(location)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val user = GriefDefender.getCore().getUser(player.uniqueId)?: return true
        return user.canPlace(player.itemInUse?: player.inventory.itemInMainHand, block.location)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val user = GriefDefender.getCore().getUser(player.uniqueId)?: return true
        return user.canHurtEntity(player.inventory.itemInMainHand, victim)
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "GriefDefender"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefWrapper) {
            return false
        }

        return other.pluginName == this.pluginName
    }

    override fun hashCode(): Int {
        return this.pluginName.hashCode()
    }
}