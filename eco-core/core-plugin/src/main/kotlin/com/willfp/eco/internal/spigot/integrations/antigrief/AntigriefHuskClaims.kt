package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import net.crashcraft.crashclaim.CrashClaim
import net.crashcraft.crashclaim.permissions.PermissionRoute
import net.william278.huskclaims.api.HuskClaimsAPI
import net.william278.huskclaims.libraries.cloplib.operation.Operation
import net.william278.huskclaims.libraries.cloplib.operation.OperationPosition
import net.william278.huskclaims.libraries.cloplib.operation.OperationType
import net.william278.huskclaims.position.Position
import net.william278.huskclaims.position.World
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import kotlin.jvm.optionals.getOrElse

class AntigriefHuskClaims : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = HuskClaimsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player.uniqueId) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                OperationType.BLOCK_BREAK,
                Position.at(
                    block.x.toDouble(),
                    block.y.toDouble(),
                    block.z.toDouble(),
                    api.getWorld(block.location.world.name)
                ),
                true
            )
        )
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val api = HuskClaimsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player.uniqueId) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                OperationType.EXPLOSION_DAMAGE_ENTITY,
                Position.at(
                    location.x,
                    location.y,
                    location.z,
                    api.getWorld(location.world.name)
                ),
                true
            )
        )
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = HuskClaimsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player.uniqueId) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                OperationType.BLOCK_PLACE,
                Position.at(
                    block.x.toDouble(),
                    block.y.toDouble(),
                    block.z.toDouble(),
                    api.getWorld(block.location.world.name)
                ),
                true
            )
        )
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val api = HuskClaimsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player.uniqueId) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                when (victim) {
                    is Monster -> OperationType.PLAYER_DAMAGE_MONSTER
                    is Player -> OperationType.PLAYER_DAMAGE_PLAYER
                    else -> OperationType.PLAYER_DAMAGE_ENTITY
                },
                Position.at(
                    victim.x,
                    victim.y,
                    victim.z,
                    api.getWorld(victim.location.world.name)
                ),
                true
            )
        )
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "HuskClaims"
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