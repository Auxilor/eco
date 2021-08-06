package com.willfp.eco.spigot.integrations.antigrief

import com.palmergames.bukkit.towny.TownyAPI
import com.palmergames.bukkit.towny.TownyUniverse
import com.palmergames.bukkit.towny.`object`.Town
import com.palmergames.bukkit.towny.`object`.TownyPermission
import com.palmergames.bukkit.towny.`object`.WorldCoord
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefTowny : AntigriefWrapper {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val world = TownyUniverse.getInstance().worldMap[block.location.world!!.name] ?: return true
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
        val world = TownyUniverse.getInstance().worldMap[location.world!!.name] ?: return true
        return if (TownyAPI.getInstance().isWilderness(location)) {
            true
        } else PlayerCacheUtil.getCachePermission(player, location, Material.TNT, TownyPermission.ActionType.ITEM_USE)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val world = TownyUniverse.getInstance().worldMap[block.location.world!!.name] ?: return true
        return if (TownyAPI.getInstance().isWilderness(block)) {
            true
        } else PlayerCacheUtil.getCachePermission(player, block.location, block.type, TownyPermission.ActionType.BUILD)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val world = TownyUniverse.getInstance().worldMap[victim.location.world!!.name] ?: return true
        if (TownyAPI.getInstance().isWilderness(victim.location)) {
            return if (victim is Player) {
                world.isPVP
            } else {
                true
            }
        }
        if (victim is Player) {
            try {
                val town: Town = WorldCoord.parseWorldCoord(victim.getLocation()).getTownBlock().getTown()
                return town.isPVP()
            } catch (ignored: Exception) {
                // If exception, no town was found, thus return true.
            }
        } else {
            try {
                val town: Town = WorldCoord.parseWorldCoord(victim.location).getTownBlock().getTown()
                return town.hasMobs()
            } catch (ignored: Exception) {
                // If exception, no town was found, thus return true.
            }
        }
        return true
    }

    override fun getPluginName(): String {
        return "Towny"
    }
}