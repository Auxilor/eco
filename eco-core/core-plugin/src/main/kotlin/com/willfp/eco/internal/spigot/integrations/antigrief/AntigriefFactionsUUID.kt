package com.willfp.eco.internal.spigot.integrations.antigrief

import com.massivecraft.factions.Board
import com.massivecraft.factions.FLocation
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.FPlayers
import com.massivecraft.factions.Faction
import com.massivecraft.factions.perms.PermissibleActions
import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

class AntigriefFactionsUUID : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val fplayer: FPlayer = FPlayers.getInstance().getByPlayer(player)
        val flocation = FLocation(block.location)
        val faction: Faction = Board.getInstance().getFactionAt(flocation)
        return if (!faction.hasAccess(fplayer, PermissibleActions.DESTROY, flocation)) {
            fplayer.isAdminBypassing
        } else true
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val flocation = FLocation(location)
        val faction: Faction = Board.getInstance().getFactionAt(flocation)
        return !faction.noExplosionsInTerritory()
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val fplayer: FPlayer = FPlayers.getInstance().getByPlayer(player)
        val flocation = FLocation(block.location)
        val faction: Faction = Board.getInstance().getFactionAt(flocation)
        return if (!faction.hasAccess(fplayer, PermissibleActions.BUILD, flocation)) {
            fplayer.isAdminBypassing
        } else true
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val fplayer: FPlayer = FPlayers.getInstance().getByPlayer(player)
        val flocation = FLocation(victim.location)
        val faction: Faction = Board.getInstance().getFactionAt(flocation)
        if (victim is Player) {
            if (faction.isPeaceful) {
                return fplayer.isAdminBypassing
            }
        } else {
            if (faction.hasAccess(fplayer, PermissibleActions.DESTROY, flocation)) {
                return fplayer.isAdminBypassing
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