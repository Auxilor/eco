package com.willfp.eco.spigot.integrations.antigrief

import com.flowpowered.math.vector.Vector3i
import com.griefdefender.api.GriefDefender
import com.griefdefender.api.claim.Claim
import com.griefdefender.api.data.PlayerData
import com.griefdefender.api.permission.flag.Flags
import com.willfp.eco.core.integrations.antigrief.AntigriefWrapper
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*

class AntigriefGriefDefender: AntigriefWrapper {

    override fun getPluginName(): String {
        return "GriefDefender"
    }

    override fun canBreakBlock(player: Player, block: Block): Boolean {

        val data: PlayerData = GriefDefender.getCore().getPlayerData(player.world.uid, player.uniqueId) ?: return true

        val claim: Claim? = GriefDefender.getCore().getClaimAt(Vector3i(block.location.x, block.location.y, block.location.z))

        return if (claim == null) {
            true
        } else {
            return if (data.canIgnoreClaim(claim)) {
                true
            } else {
                claim.getActiveFlagPermissionValue(Flags.BLOCK_BREAK, data.user, Collections.emptySet(), true).asBoolean()
            }
        }

    }

    override fun canCreateExplosion(player: Player, location: Location): Boolean {

        val data: PlayerData = GriefDefender.getCore().getPlayerData(player.world.uid, player.uniqueId) ?: return true

        val claim: Claim? = GriefDefender.getCore().getClaimAt(Vector3i(location.x, location.y, location.z))

        return if (claim == null) {
            true
        } else {
            return if (data.canIgnoreClaim(claim)) {
                true
            } else {
                claim.getActiveFlagPermissionValue(Flags.EXPLOSION_BLOCK, data.user, Collections.emptySet(), true).asBoolean()
            }
        }

    }

    override fun canPlaceBlock(player: Player, block: Block): Boolean {

        val data: PlayerData = GriefDefender.getCore().getPlayerData(player.world.uid, player.uniqueId) ?: return true

        val claim: Claim? = GriefDefender.getCore().getClaimAt(Vector3i(block.location.x, block.location.y, block.location.z))

        return if (claim == null) {
            true
        } else {
            return if (data.canIgnoreClaim(claim)) {
                true
            } else {
                claim.getActiveFlagPermissionValue(Flags.BLOCK_PLACE, data.user, Collections.emptySet(), true).asBoolean()
            }
        }

    }

    override fun canInjure(player: Player, victim: LivingEntity): Boolean {

        val data: PlayerData = GriefDefender.getCore().getPlayerData(player.world.uid, player.uniqueId) ?: return true

        if (data.canIgnoreClaim(data.currentClaim)) return true

        return if (victim is Player) {
            val vicData: PlayerData? = GriefDefender.getCore().getPlayerData(victim.world.uid, victim.uniqueId)
            if (vicData != null) {
                data.canPvp(data.currentClaim) and data.canPvp(vicData.currentClaim)
            } else data.canPvp(data.currentClaim)
        } else {
            val claim: Claim? = GriefDefender.getCore().getClaimAt(Vector3i(victim.location.x, victim.location.y, victim.location.z))
            claim?.getActiveFlagPermissionValue(Flags.ENTITY_DAMAGE, data.user, Collections.emptySet(), true)
                ?.asBoolean() ?: true
        }
    }

}