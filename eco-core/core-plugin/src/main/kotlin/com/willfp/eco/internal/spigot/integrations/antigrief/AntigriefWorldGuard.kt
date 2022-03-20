package com.willfp.eco.internal.spigot.integrations.antigrief

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
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefWorldGuard : AntigriefWrapper {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        return if (!query.testState(
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
        return if (!query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.TNT)) {
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
        return if (!query.testState(
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
        val flag = when(victim) {
            is Player -> Flags.PVP
            is Monster -> Flags.MOB_DAMAGE
            is Animals -> Flags.DAMAGE_ANIMALS
            else -> return true
        }

        return if (!query.testState(BukkitAdapter.adapt(victim.location), localPlayer, flag)) {
            WorldGuard.getInstance().platform.sessionManager.hasBypass(
                localPlayer,
                BukkitAdapter.adapt(player.world)
            )
        } else {
            true
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val localPlayer: LocalPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
        val query: RegionQuery = container.createQuery()
        val world = location.world
        Validate.notNull(world, "World cannot be null!")
        return if (!query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.ITEM_PICKUP)) {
            WorldGuard.getInstance().platform.sessionManager.hasBypass(
                localPlayer,
                BukkitAdapter.adapt(world)
            )
        } else true
    }

    override fun getPluginName(): String {
        return "WorldGuard"
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