package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import net.crashcraft.crashclaim.CrashClaim
import net.crashcraft.crashclaim.permissions.PermissionRoute
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefCrashClaim : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = CrashClaim.getPlugin().api ?: return true
        val claim = api.getClaim(block.location) ?: return true
        return claim.hasPermission(player.uniqueId, block.location, PermissionRoute.BUILD)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val api = CrashClaim.getPlugin().api ?: return true
        val claim = api.getClaim(location) ?: return true
        return claim.hasPermission(player.uniqueId, location, PermissionRoute.EXPLOSIONS)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = CrashClaim.getPlugin().api ?: return true
        val claim = api.getClaim(block.location) ?: return true
        return claim.hasPermission(player.uniqueId, block.location, PermissionRoute.BUILD)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val api = CrashClaim.getPlugin().api ?: return true
        val claim = api.getClaim(victim.location) ?: return true
        return when (victim) {
            is Player -> false
            else -> claim.hasPermission(player.uniqueId, victim.location, PermissionRoute.ENTITIES)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "CrashClaim"
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