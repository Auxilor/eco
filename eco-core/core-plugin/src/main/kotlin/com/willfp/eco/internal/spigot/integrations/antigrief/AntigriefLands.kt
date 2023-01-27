package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import me.angeschossen.lands.api.LandsIntegration
import me.angeschossen.lands.api.flags.enums.FlagTarget
import me.angeschossen.lands.api.flags.enums.RoleFlagCategory
import me.angeschossen.lands.api.flags.type.RoleFlag
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
        return area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
            FlagTarget.PLAYER,
            RoleFlagCategory.ACTION,
            "BLOCK_BREAK"), block.type, false)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = landsIntegration.getArea(location) ?: return true
        return  area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
            FlagTarget.PLAYER,
            RoleFlagCategory.ACTION,
            "ATTACK_PLAYER"), Material.AIR, false) && area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
            FlagTarget.PLAYER,
            RoleFlagCategory.ACTION,
            "ATTACK_ANIMAL"), Material.AIR, false)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getArea(block.location) ?: return true
        return area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
            FlagTarget.PLAYER,
            RoleFlagCategory.ACTION,
            "BLOCK_PLACE"), Material.AIR, false)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {

        val area = landsIntegration.getArea(victim.location) ?: return true

        return when(victim) {
            is Player -> area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
                FlagTarget.PLAYER,
                RoleFlagCategory.ACTION,
                "ATTACK_PLAYER"), Material.AIR, false)
            is Monster -> area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
                FlagTarget.PLAYER,
                RoleFlagCategory.ACTION,
                "ATTACK_MONSTER"), Material.AIR, false)
            is Animals -> area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
                FlagTarget.PLAYER,
                RoleFlagCategory.ACTION,
                "ATTACK_ANIMAL"), Material.AIR, false)
            else -> area.isTrusted(player.uniqueId)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val area = landsIntegration.getArea(location) ?: return true
        return area.hasRoleFlag(player, RoleFlag.of(landsIntegration,
            FlagTarget.PLAYER,
            RoleFlagCategory.ACTION,
            "ITEM_PICKUP"), Material.AIR, false)
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