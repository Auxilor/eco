package com.willfp.eco.internal.spigot.integrations.antigrief

import com.songoda.skyblock.SkyBlock
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefFabledSkyBlock : AntigriefIntegration {

    override fun getPluginName(): String {
        return "FabledSkyBlock"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        val skyblock = SkyBlock.getInstance()
        val island = skyblock.islandManager.getIslandAtLocation(block.location) ?: return true

        if (player.hasPermission("fabledskyblock.bypass.destroy")) {
            return true
        }

        if (skyblock.permissionManager.hasPermission(island, "Destroy", island.getRole(player))) {
            return true
        }

        return false
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        val skyblock = SkyBlock.getInstance()
        val island = skyblock.islandManager.getIslandAtLocation(location) ?: return true

        if (player.hasPermission("fabledskyblock.bypass.explosions")) {
            return true
        }

        if (skyblock.permissionManager.hasPermission(island, "Explosions", island.getRole(player))) {
            return true
        }

        return false
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        val skyblock = SkyBlock.getInstance()
        val island = skyblock.islandManager.getIslandAtLocation(block.location) ?: return true

        if (player.hasPermission("fabledskyblock.bypass.place")) {
            return true
        }

        if (skyblock.permissionManager.hasPermission(island, "Place", island.getRole(player))) {
            return true
        }

        return false
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        val skyblock = SkyBlock.getInstance()
        val island = SkyBlock.getInstance().islandManager.getIslandAtLocation(victim.location) ?: return true

        if (victim is Player) return skyblock.permissionManager.hasPermission(island, "PvP", island.getRole(player))

        val islandPermission = when (victim) {
            is Monster -> "MonsterHurting"
            else -> "MobHurting"
        }

        if (skyblock.permissionManager.hasPermission(island, islandPermission, island.getRole(player))) {
            return true
        }

        return false
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val skyblock = SkyBlock.getInstance()
        val island = SkyBlock.getInstance().islandManager.getIslandAtLocation(location) ?: return true

        if (player.hasPermission("fabledskyblock.bypass.itempickup")) {
            return true
        }

        if (skyblock.permissionManager.hasPermission(island, "ItemPickup", island.getRole(player))) {
            return true
        }

        return false
    }
}
