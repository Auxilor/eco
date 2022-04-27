package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import me.ryanhamshire.GriefPrevention.Claim
import me.ryanhamshire.GriefPrevention.GriefPrevention
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefGriefPrevention : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val claim: Claim? = GriefPrevention.instance.dataStore.getClaimAt(block.location, false, null)
        return if (claim != null) {
            claim.allowBreak(player, block.type) == null
        } else true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val claim: Claim? = GriefPrevention.instance.dataStore.getClaimAt(location, false, null)
        return claim?.areExplosivesAllowed ?: true
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val claim: Claim? = GriefPrevention.instance.dataStore.getClaimAt(block.location, false, null)
        return if (claim != null) {
            claim.allowBuild(player, block.type) == null
        } else true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val claim: Claim? = GriefPrevention.instance.dataStore.getClaimAt(victim.location, false, null)
        return if (victim is Player) {
            claim == null
        } else {
            if (claim?.ownerID != null) {
                claim.ownerID == player.uniqueId
            } else true
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "GriefPrevention"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefIntegration) {
            return false
        }

        return other.pluginName == this.pluginName
    }

    override fun hashCode(): Int {
        return this.pluginName.hashCode()
    }
}