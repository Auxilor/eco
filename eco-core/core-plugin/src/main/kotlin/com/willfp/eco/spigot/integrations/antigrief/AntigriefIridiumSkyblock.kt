package com.willfp.eco.spigot.integrations.antigrief

import com.iridium.iridiumskyblock.PermissionType
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefIridiumSkyblock : AntigriefWrapper {
    private val api = IridiumSkyblockAPI.getInstance()

    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        return api.getIslandPermission(api.getIslandViaLocation(block.location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_BREAK)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        return api.getIslandPermission(api.getIslandViaLocation(location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_BREAK)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        return api.getIslandPermission(api.getIslandViaLocation(block.location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_PLACE)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        return when (victim) {
            is Player -> api.getIslandViaLocation(victim.location).orElse(null) != null
            else -> api.getIslandPermission(api.getIslandViaLocation(victim.location).orElse(null) ?: return true, api.getUser(player), PermissionType.KILL_MOBS)
        }
    }

    override fun getPluginName(): String {
        return "IridiumSkyblock"
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