package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import me.angeschossen.lands.api.LandsIntegration
import me.angeschossen.lands.api.flags.type.Flags
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefLands(private val plugin: EcoPlugin) : AntigriefIntegration {
    private val landsIntegration = LandsIntegration.of(this.plugin)
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getArea(block.location) ?: return true
        val landPlayer = landsIntegration.getLandPlayer(player.uniqueId)
        return area.hasRoleFlag(landPlayer, Flags.BLOCK_BREAK, block.type, false)
                || area.hasRoleFlag(landPlayer, Flags.HARVEST, block.type, false)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = landsIntegration.getArea(location) ?: return true
        val landPlayer = landsIntegration.getLandPlayer(player.uniqueId)
        return area.hasRoleFlag(landPlayer, Flags.ATTACK_PLAYER, Material.AIR, false)
                && area.hasRoleFlag(landPlayer, Flags.ATTACK_ANIMAL, Material.AIR, false)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getArea(block.location) ?: return true
        val landPlayer = landsIntegration.getLandPlayer(player.uniqueId)
        return area.hasRoleFlag(landPlayer, Flags.BLOCK_PLACE, block.type, false)
                || area.hasRoleFlag(landPlayer, Flags.PLANT, block.type, false)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {

        val area = landsIntegration.getArea(victim.location) ?: return true
        val landPlayer = landsIntegration.getLandPlayer(player.uniqueId)

        return when (victim) {
            is Player -> area.hasRoleFlag(landPlayer, Flags.ATTACK_PLAYER, Material.AIR, false)
            is Monster -> area.hasRoleFlag(landPlayer, Flags.ATTACK_MONSTER, Material.AIR, false)
            is Animals -> area.hasRoleFlag(landPlayer, Flags.ATTACK_ANIMAL, Material.AIR, false)
            else -> area.isTrusted(player.uniqueId)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val area = landsIntegration.getArea(location) ?: return true
        val landPlayer = landsIntegration.getLandPlayer(player.uniqueId)
        return area.hasRoleFlag(landPlayer, Flags.ITEM_PICKUP, Material.AIR, false)
    }

    override fun getPluginName(): String {
        return "Lands"
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