package com.willfp.eco.internal.spigot.integrations.antigrief

import dev.kitteh.factions.Board
import dev.kitteh.factions.FLocation
import dev.kitteh.factions.FPlayer
import dev.kitteh.factions.FPlayers
import dev.kitteh.factions.Faction
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import dev.kitteh.factions.permissible.PermissibleActions
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefFactionsUUID : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val fplayer: FPlayer = FPlayers.fPlayers().get(player)
        val flocation = FLocation(block.location)
        val faction: Faction = Board.board().factionAt(flocation)
        return if (!faction.hasAccess(fplayer, PermissibleActions.DESTROY, flocation)) {
            fplayer.adminBypass()
        } else true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val flocation = FLocation(location)
        val faction: Faction = Board.board().factionAt(flocation)
        return !faction.noExplosionsInTerritory()
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val fplayer: FPlayer = FPlayers.fPlayers().get(player)
        val flocation = FLocation(block.location)
        val faction: Faction = Board.board().factionAt(flocation)
        return if (!faction.hasAccess(fplayer, PermissibleActions.BUILD, flocation)) {
            fplayer.adminBypass()
        } else true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val fplayer: FPlayer = FPlayers.fPlayers().get(player)
        val flocation = FLocation(victim.location)
        val faction: Faction = Board.board().factionAt(flocation)
        if (victim is Player) {
            if (faction.isPeaceful) {
                return fplayer.adminBypass()
            }
        } else {
            if (!faction.hasAccess(fplayer, PermissibleActions.DESTROY, flocation)) {
                return fplayer.adminBypass()
            }
        }
        return true
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "FactionsUUID"
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