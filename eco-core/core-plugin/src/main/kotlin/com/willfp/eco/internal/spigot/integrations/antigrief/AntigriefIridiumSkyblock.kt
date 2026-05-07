package com.willfp.eco.internal.spigot.integrations.antigrief

import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI
import com.iridium.iridiumskyblock.dependencies.iridiumteams.PermissionType
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefIridiumSkyblock : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = IridiumSkyblockAPI.getInstance() ?: return false

        return api.getIslandPermission(api.getIslandViaLocation(block.location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_BREAK)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val api = IridiumSkyblockAPI.getInstance() ?: return false

        return api.getIslandPermission(api.getIslandViaLocation(location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_BREAK)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = IridiumSkyblockAPI.getInstance() ?: return false

        return api.getIslandPermission(api.getIslandViaLocation(block.location).orElse(null) ?: return true, api.getUser(player), PermissionType.BLOCK_PLACE)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val api = IridiumSkyblockAPI.getInstance() ?: return false

        return when (victim) {
            is Player -> api.getIslandViaLocation(victim.location).orElse(null) != null
            else -> api.getIslandPermission(api.getIslandViaLocation(victim.location).orElse(null) ?: return true, api.getUser(player), PermissionType.KILL_MOBS)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
        /*
        val api = IridiumSkyblockAPI.getInstance()
        return api.getIslandPermission(api.getIslandViaLocation(location).orElse(null) ?: return true, api.getUser(player), PermissionType.PICKUP_ITEMS)

        PICKUP_ITEMS was removed in Iridium v4
         */
    }

    override fun getPluginName(): String {
        return "IridiumSkyblock"
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