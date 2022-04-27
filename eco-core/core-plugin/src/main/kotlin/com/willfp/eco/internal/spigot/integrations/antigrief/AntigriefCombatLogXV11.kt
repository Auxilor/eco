package com.willfp.eco.internal.spigot.integrations.antigrief

import com.github.sirblobman.combatlogx.api.ICombatLogX
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import combatlogx.expansion.newbie.helper.NewbieHelperExpansion
import combatlogx.expansion.newbie.helper.manager.PVPManager
import combatlogx.expansion.newbie.helper.manager.ProtectionManager
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefCombatLogXV11 : AntigriefIntegration {
    private val instance: ICombatLogX = Bukkit.getPluginManager().getPlugin("CombatLogX") as ICombatLogX
    private var disabled = false

    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        return true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        return true
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        return true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        if (victim !is Player) {
            return true
        }

        if (disabled) {
            return true
        }

        // Only run checks if the NewbieHelper expansion is installed on the server.
        val expansionManager = instance.expansionManager
        val optionalExpansion = expansionManager.getExpansion("NewbieHelper")
        if (optionalExpansion.isPresent) {
            val newbieHelperExpansion = runCatching {
                optionalExpansion.get() as NewbieHelperExpansion
            }.onFailure { disabled = true }.getOrNull() ?: return true
            val protectionManager: ProtectionManager = newbieHelperExpansion.protectionManager
            val pvpManager: PVPManager = newbieHelperExpansion.pvpManager
            val victimProtected: Boolean = protectionManager.isProtected(victim)
            val victimDisabledPvP: Boolean = pvpManager.isDisabled(victim)
            val playerDisabledPvp: Boolean = pvpManager.isDisabled(player)
            return !victimProtected && !victimDisabledPvP && !playerDisabledPvp
        }
        return true
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "CombatLogX"
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