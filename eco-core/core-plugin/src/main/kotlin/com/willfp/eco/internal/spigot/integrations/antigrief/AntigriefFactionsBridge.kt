package com.willfp.eco.internal.spigot.integrations.antigrief

import cc.javajobs.factionsbridge.FactionsBridge
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefFactionsBridge : AntigriefIntegration {
    private fun canAct(player: Player, location: Location): Boolean {
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val faction = api.getFactionAt(location) ?: return true
        if (faction.isWilderness) return true
        if (faction.isSafeZone) return false
        val fPlayer = api.getFPlayer(player)
        val playerFaction = fPlayer.faction ?: return false
        return faction.id == playerFaction.id
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {
        return canAct(player, block.location)
    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {
        return canAct(player, block.location)
    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val faction = api.getFactionAt(location) ?: return true
        return !faction.isSafeZone
    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {
        val api = FactionsBridge.getFactionsAPI() ?: return true
        val faction = api.getFactionAt(victim.location) ?: return true
        if (faction.isSafeZone) return false
        if (victim is Player && faction.isPeaceful) return false
        return true
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "FactionsBridge"
    }

    override fun equals(other: Any?): Boolean {
        if (other !is AntigriefIntegration) return false
        return other.pluginName == this.pluginName
    }

    override fun hashCode(): Int {
        return this.pluginName.hashCode()
    }
}