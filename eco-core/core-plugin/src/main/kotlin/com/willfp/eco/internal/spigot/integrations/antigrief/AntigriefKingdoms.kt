package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.kingdoms.constants.group.Kingdom
import org.kingdoms.constants.land.Land
import org.kingdoms.constants.land.location.SimpleChunkLocation
import org.kingdoms.constants.player.KingdomPlayer
import org.kingdoms.managers.PvPManager
import org.kingdoms.managers.land.BuildingProcessor

class AntigriefKingdoms : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val kp: KingdomPlayer = KingdomPlayer.getKingdomPlayer(player)
        if (kp.isAdmin) {
            return true
        }
        return BuildingProcessor(
            player,
            kp,
            SimpleChunkLocation.of(block),
            false
        ).process().isSuccessful
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        return canBreakBlock(player, location.block)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val kp: KingdomPlayer = KingdomPlayer.getKingdomPlayer(player)
        if (kp.isAdmin) {
            return true
        }
        return BuildingProcessor(
            player,
            kp,
            SimpleChunkLocation.of(block),
            true
        ).process().isSuccessful
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        return if (victim is Player) {
            PvPManager.canFight(player, victim)
        } else {
            val land = Land.getLand(victim.location) ?: return true
            if (!land.isClaimed) {
                return true
            }
            val kingdom: Kingdom = land.kingdom
            kingdom.isMember(player)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "Kingdoms"
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
