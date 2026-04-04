package com.willfp.eco.internal.spigot.integrations.antigrief

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI
import com.bgsoftware.superiorskyblock.api.enums.HitActionResult
import com.bgsoftware.superiorskyblock.api.island.Island
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefSuperiorSkyblock2 : AntigriefIntegration {
    override fun getPluginName(): String {
        return "SuperiorSkyblock2"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(block.location)
        val superiorPlayer = SuperiorSkyblockAPI.getPlayer(player)

        if (island == null) {
            return superiorPlayer.hasBypassModeEnabled() || !SuperiorSkyblockAPI.getSuperiorSkyblock().grid
                .isIslandsWorld(superiorPlayer.world)
        }

        if (!island.hasPermission(superiorPlayer, IslandPrivilege.getByName("BREAK"))) {
            return false
        }

        if (!island.isInsideRange(block.location)) {
            return false
        }

        return true
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) {
            return true
        }
        return SuperiorSkyblockAPI.getIslandAt(location)?.isMember(SuperiorSkyblockAPI.getPlayer(player)) ?: true
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        val island = SuperiorSkyblockAPI.getIslandAt(block.location)
        val superiorPlayer: SuperiorPlayer = SuperiorSkyblockAPI.getPlayer(player)

        if (island == null) {
            return superiorPlayer.hasBypassModeEnabled() || !SuperiorSkyblockAPI.getSuperiorSkyblock().grid
                .isIslandsWorld(superiorPlayer.world)
        }

        if (!island.hasPermission(superiorPlayer, IslandPrivilege.getByName("BUILD"))) {
            return false
        }

        if (!island.isInsideRange(block.location)) {
            return false
        }

        return true
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {

        val island: Island? = SuperiorSkyblockAPI.getSuperiorSkyblock().grid.getIslandAt(victim.location)

        if (victim is Player) return SuperiorSkyblockAPI.getPlayer(player).canHit(SuperiorSkyblockAPI.getPlayer(victim))
            .equals(HitActionResult.SUCCESS)

        val islandPermission = when (victim) {
            is Monster -> IslandPrivilege.getByName("MONSTER_DAMAGE")
            else -> IslandPrivilege.getByName("ANIMAL_DAMAGE")
        }

        return (island == null || island.hasPermission(player, islandPermission))
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val superiorPlayer: SuperiorPlayer =
            SuperiorSkyblockAPI.getPlayer(player)
        val island = SuperiorSkyblockAPI.getSuperiorSkyblock().grid.getIslandAt(location) ?: return true
        return island.hasPermission(superiorPlayer, IslandPrivilege.getByName("PICKUP_DROPS"))
    }
}