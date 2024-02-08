package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import net.william278.husktowns.api.HuskTownsAPI
import net.william278.husktowns.claim.Position
import net.william278.husktowns.listener.Operation
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player

class AntigriefHuskTowns : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = HuskTownsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                Operation.Type.BLOCK_BREAK,
                Position.at(
                    block.location.x,
                    block.location.y,
                    block.location.z,
                    api.getWorld(block.world)
                ),
                true
            )
        )
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val api = HuskTownsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                Operation.Type.EXPLOSION_DAMAGE_ENTITY,
                Position.at(
                    location.x,
                    location.y,
                    location.z,
                    api.getWorld(location.world)
                ),
                true
            )
        )
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val api = HuskTownsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                Operation.Type.BLOCK_PLACE,
                Position.at(
                    block.location.x,
                    block.location.y,
                    block.location.z,
                    api.getWorld(block.world)
                ),
                true
            )
        )
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val api = HuskTownsAPI.getInstance() ?: return true

        val user = api.getOnlineUser(player) ?: return true

        return api.isOperationAllowed(
            Operation.of(
                user,
                when(victim) {
                    is Monster -> Operation.Type.PLAYER_DAMAGE_MONSTER
                    is Player -> Operation.Type.PLAYER_DAMAGE_PLAYER
                    else -> Operation.Type.PLACE_HANGING_ENTITY
                },
                Position.at(
                    player.location.x,
                    player.location.y,
                    player.location.z,
                    api.getWorld(player.world)
                ),
                true
            )
        )
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "HuskTowns"
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