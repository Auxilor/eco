package com.willfp.eco.spigot.integrations.antigrief

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import me.angeschossen.lands.api.integration.LandsIntegration
import me.angeschossen.lands.api.role.enums.RoleSetting
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefLands(plugin: EcoPlugin) : PluginDependent<EcoPlugin?>(plugin), AntigriefWrapper {
    private val landsIntegration = LandsIntegration(this.plugin!!)
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location)
        return area?.canSetting(player, RoleSetting.BLOCK_BREAK, false) ?: true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(location)
        return area?.canSetting(player, RoleSetting.BLOCK_IGNITE, false) ?: true
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(block.location)
        return area?.canSetting(player, RoleSetting.BLOCK_PLACE, false) ?: true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val area = landsIntegration.getAreaByLoc(victim.location)
        if (victim is Player) {
            if (area != null) {
                return area.canSetting(player, RoleSetting.ATTACK_PLAYER, false)
            }
        } else {
            if (area != null) {
                return area.canSetting(player, RoleSetting.ATTACK_ANIMAL, false)
            }
        }
        return true
    }

    override fun getPluginName(): String {
        return "Lands"
    }
}