package com.willfp.eco.spigot.integrations.antigrief

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI
import com.bgsoftware.superiorskyblock.api.enums.HitActionResult
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefSuperiorSkyblock2 : AntigriefWrapper {
    override fun getPluginName(): String {
        return "SuperiorSkyblock2"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) return true
        return SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("Break"))
                || SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("BREAK"))
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) return true
        return SuperiorSkyblockAPI.getIslandAt(location)?.isMember(SuperiorSkyblockAPI.getPlayer(player)) ?: true
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) return true
        return SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("Place"))
                || SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("PLACE"))
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) return true
        return when (victim) {
            is Player -> SuperiorSkyblockAPI.getPlayer(player).canHit(SuperiorSkyblockAPI.getPlayer(victim)).equals(HitActionResult.SUCCESS)
            is Animals -> {
                return SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("ANIMAL_DAMAGE"))
            }
            is Monster -> {
                return SuperiorSkyblockAPI.getPlayer(player).hasPermission(IslandPrivilege.getByName("MONSTER_DAMAGE"))
            }
            else -> true
        }
    }
}