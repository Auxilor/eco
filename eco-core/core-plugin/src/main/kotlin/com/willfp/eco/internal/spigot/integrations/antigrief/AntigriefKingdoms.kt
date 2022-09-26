package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.kingdoms.constants.group.Kingdom
import org.kingdoms.constants.group.model.relationships.StandardRelationAttribute
import org.kingdoms.constants.land.Land
import org.kingdoms.constants.player.KingdomPlayer
import org.kingdoms.constants.player.StandardKingdomPermission
import org.kingdoms.managers.PvPManager

class AntigriefKingdoms : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val kp: KingdomPlayer = KingdomPlayer.getKingdomPlayer(player)
        if (kp.isAdmin) {
            return true
        }
        val kingdom: Kingdom = kp.kingdom ?: return false
        val land = Land.getLand(block) ?: return true
        val permission = if (land.isNexusLand) {
            StandardKingdomPermission.NEXUS_BUILD
        } else StandardKingdomPermission.BUILD

        return if (!kp.hasPermission(permission)) {
            false
        } else kingdom.hasAttribute(land.kingdom, StandardRelationAttribute.BUILD)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val land = Land.getLand(location) ?: return true
        if (!land.isClaimed) {
            return true
        }
        val kingdom: Kingdom = land.kingdom
        return kingdom.isMember(player)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        return canBreakBlock(player, block)
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
