package com.willfp.eco.spigot.integrations.antigrief

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.LocalPlayer
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.RegionContainer
import com.sk89q.worldguard.protection.regions.RegionQuery
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.apache.commons.lang.Validate
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefWorldGuard : AntigriefWrapper {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        return if (!query.testBuild(
                BukkitAdapter.adapt(block.location),
                localPlayer,
                Flags.BLOCK_BREAK
            )
        ) {
            WorldGuard.getInstance().platform.sessionManager.hasBypass(
                localPlayer,
                BukkitAdapter.adapt(block.world)
            )
        } else true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        val world = location.world
        Validate.notNull(world, "World cannot be null!")
        return if (!query.testBuild(BukkitAdapter.adapt(location), localPlayer, Flags.TNT)) {
            WorldGuard.getInstance().platform.sessionManager.hasBypass(
                localPlayer,
                BukkitAdapter.adapt(world)
            )
        } else true
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        return if (!query.testBuild(
                BukkitAdapter.adapt(block.location),
                localPlayer,
                Flags.BLOCK_PLACE
            )
        ) {
            WorldGuard.getInstance().platform.sessionManager.hasBypass(
                localPlayer,
                BukkitAdapter.adapt(block.world)
            )
        } else true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        if (victim is Player) {
            if (!query.testBuild(BukkitAdapter.adapt(victim.getLocation()), localPlayer, Flags.PVP)) {
                return WorldGuard.getInstance().platform.sessionManager.hasBypass(
                    localPlayer,
                    BukkitAdapter.adapt(player.world)
                )
            }
        } else {
            if (!query.testBuild(BukkitAdapter.adapt(victim.location), localPlayer, Flags.DAMAGE_ANIMALS)) {
                return WorldGuard.getInstance().platform.sessionManager.hasBypass(
                    localPlayer,
                    BukkitAdapter.adapt(player.world)
                )
            }
        }
        return true
    }

    override fun getPluginName(): String {
        return "WorldGuard"
    }
}