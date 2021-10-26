package com.willfp.eco.spigot.integrations.antigrief

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import me.angeschossen.lands.api.integration.LandsIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefLands(private val plugin: EcoPlugin) : AntigriefWrapper {
    private val landsIntegration = LandsIntegration(this.plugin)
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location) ?: return true
        return area.isTrusted(player.uniqueId)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(location) ?: return true
        return area.isTrusted(player.uniqueId)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location) ?: return true
        return area.isTrusted(player.uniqueId)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(victim.location) ?: return true
        return area.isTrusted(player.uniqueId)
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