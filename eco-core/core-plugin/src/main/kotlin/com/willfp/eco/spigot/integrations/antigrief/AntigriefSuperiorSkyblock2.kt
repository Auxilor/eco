package com.willfp.eco.spigot.integrations.antigrief

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI
import com.bgsoftware.superiorskyblock.api.enums.HitActionResult
import com.bgsoftware.superiorskyblock.api.island.Island
import com.bgsoftware.superiorskyblock.api.island.IslandPrivilege
import com.bgsoftware.superiorskyblock.api.key.Key
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.*

class AntigriefSuperiorSkyblock2 : AntigriefWrapper {
    override fun getPluginName(): String {
        return "SuperiorSkyblock2"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        val island: Island? =
            SuperiorSkyblockAPI.getIslandAt(block.location)

        val superiorPlayer: SuperiorPlayer =
            SuperiorSkyblockAPI.getPlayer(player)

        if (island == null) {
            if (!superiorPlayer.hasBypassModeEnabled() && SuperiorSkyblockAPI.getSuperiorSkyblock().grid
                    .isIslandsWorld(player.world)
            ) {
                return false
            }
            return true
        }

        val blockType = block.type

        val islandPermission: IslandPrivilege =
            if (blockType == Material.SPAWNER) IslandPrivilege.getByName("SPAWNER_BREAK") else IslandPrivilege.getByName("BREAK")

        if (!island.hasPermission(superiorPlayer, islandPermission)) {
            return false
        }

        if (SuperiorSkyblockAPI.getSuperiorSkyblock().settings.valuableBlocks
                .contains(Key.of(block)) &&
            !island.hasPermission(superiorPlayer, IslandPrivilege.getByName("VALUABLE_BREAK"))
        ) {
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
        val island: Island? =
            SuperiorSkyblockAPI.getIslandAt(block.location)

        val superiorPlayer: SuperiorPlayer =
            SuperiorSkyblockAPI.getPlayer(player)

        if (island == null) {
            if (!superiorPlayer.hasBypassModeEnabled() && SuperiorSkyblockAPI.getSuperiorSkyblock().grid.isIslandsWorld(superiorPlayer.world)
            ) {
                return false
            }
            return true
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
        if (SuperiorSkyblockAPI.getPlayer(player).hasBypassModeEnabled()) {
            return true
        }
        return when (victim) {
            is Player -> SuperiorSkyblockAPI.getPlayer(player).canHit(SuperiorSkyblockAPI.getPlayer(victim)).equals(HitActionResult.SUCCESS)
            else -> {
                val island: Island? = SuperiorSkyblockAPI.getSuperiorSkyblock().grid.getIslandAt(victim.location)
                val islandPermission = if (victim is Monster) IslandPrivilege.getByName("MONSTER_DAMAGE") else IslandPrivilege.getByName("ANIMAL_DAMAGE")
                if (island != null ) {
                    return island.hasPermission(player, islandPermission)
                }
                return true
            }
        }
    }
}