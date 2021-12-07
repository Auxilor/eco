package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import me.angeschossen.lands.api.flags.Flags
import me.angeschossen.lands.api.integration.LandsIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Animals
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefLands(private val plugin: EcoPlugin) : AntigriefWrapper {
    private val landsIntegration = LandsIntegration(this.plugin)
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location) ?: return true
        return area.hasFlag(player, Flags.BLOCK_BREAK, false)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(location) ?: return true
        return area.hasFlag(player, Flags.ATTACK_PLAYER, false) && area.hasFlag(player, Flags.ATTACK_ANIMAL, false)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location) ?: return true
        return area.hasFlag(player, Flags.BLOCK_PLACE, false)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {

        val area = landsIntegration.getAreaByLoc(victim.location) ?: return true

        return when(victim) {
            is Player -> area.hasFlag(player, Flags.ATTACK_PLAYER, false)
            is Monster -> area.hasFlag(player, Flags.ATTACK_MONSTER, false)
            is Animals -> area.hasFlag(player, Flags.ATTACK_MONSTER, false)
            else -> area.isTrusted(player.uniqueId)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val area = landsIntegration.getAreaByLoc(location) ?: return true
        return area.hasFlag(player, Flags.ITEM_PICKUP, false)
    }

    override fun getPluginName(): String {
        return "Lands"
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