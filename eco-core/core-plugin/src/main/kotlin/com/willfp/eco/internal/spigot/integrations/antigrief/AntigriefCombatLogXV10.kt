package com.willfp.eco.internal.spigot.integrations.antigrief

import com.SirBlobman.combatlogx.api.ICombatLogX
import com.SirBlobman.combatlogx.api.expansion.Expansion
import com.SirBlobman.combatlogx.expansion.newbie.helper.NewbieHelper
import com.SirBlobman.combatlogx.expansion.newbie.helper.listener.ListenerPVP
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefCombatLogXV10 : AntigriefIntegration {
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
        val optionalExpansion = expansionManager.getExpansionByName<Expansion>("NewbieHelper")
        if (optionalExpansion.isPresent) {
            val expansion = runCatching { optionalExpansion.get() }
                .onFailure { disabled = true }
                .getOrNull() ?: return true
            val newbieHelper: NewbieHelper = expansion as NewbieHelper
            val pvpListener: ListenerPVP = newbieHelper.pvpListener
            return pvpListener.isPVPEnabled(player) && pvpListener.isPVPEnabled(victim)
        } else {
            return true
        }
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