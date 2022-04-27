package com.willfp.eco.internal.spigot.integrations.antigrief

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.TownyUniverse
import com.palmergames.bukkit.towny.`object`.TownyPermission
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefTowny : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        TownyUniverse.getInstance().worldMap[block.location.world!!.name] ?: return true
        return if (TownyAPI.getInstance().isWilderness(block)) {
            true
        } else PlayerCacheUtil.getCachePermission(
            player,
            block.location,
            block.type,
            TownyPermission.ActionType.DESTROY
        )
                || PlayerCacheUtil.getCachePermission(
            player,
            block.location,
            block.type,
            TownyPermission.ActionType.BUILD
        )
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        TownyUniverse.getInstance().worldMap[location.world?.name] ?: return true
        return if (TownyAPI.getInstance().isWilderness(location)) {
            true
        } else PlayerCacheUtil.getCachePermission(player, location, Material.TNT, TownyPermission.ActionType.ITEM_USE)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        TownyUniverse.getInstance().worldMap[block.location.world?.name] ?: return true
        return if (TownyAPI.getInstance().isWilderness(block)) {
            true
        } else PlayerCacheUtil.getCachePermission(player, block.location, block.type, TownyPermission.ActionType.BUILD)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val world = TownyUniverse.getInstance().worldMap[victim.location.world?.name] ?: return true
        if (TownyAPI.getInstance().isWilderness(victim.location)) {
            return if (victim is Player) {
                world.isPVP
            } else {
                true
            }
        }
        val townBlock = TownyAPI.getInstance().getTownBlock(victim.location) ?: return true
        return townBlock.permissions.pvp
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "Towny"
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