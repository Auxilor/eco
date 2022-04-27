package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import world.bentobox.bentobox.BentoBox
import world.bentobox.bentobox.api.user.User
import world.bentobox.bentobox.lists.Flags

class AntigriefBentoBox : AntigriefIntegration {
    override fun canBreakBlock(
        player: Player,
        block: Block
    ): Boolean {
        val island = BentoBox.getInstance().islandsManager.getIslandAt(block.location).orElse(null) ?: return true
        return island.isAllowed(User.getInstance(player), Flags.BREAK_BLOCKS)
    }

    override fun canCreateExplosion(
        player: Player,
        location: Location
    ): Boolean {
        val island = BentoBox.getInstance().islandsManager.getIslandAt(location).orElse(null) ?: return true
        return island.isAllowed(User.getInstance(player), Flags.TNT_DAMAGE)
    }

    override fun canPlaceBlock(
        player: Player,
        block: Block
    ): Boolean {
        val island = BentoBox.getInstance().islandsManager.getIslandAt(block.location).orElse(null) ?: return true
        return island.isAllowed(User.getInstance(player), Flags.PLACE_BLOCKS)
    }

    override fun canInjure(
        player: Player,
        victim: LivingEntity
    ): Boolean {
        val island = BentoBox.getInstance().islandsManager.getIslandAt(victim.location).orElse(null) ?: return true
        return when (victim) {
            is Player -> {
                island.isAllowed(
                    User.getInstance(player), when (victim.world.environment) {
                        World.Environment.NORMAL -> Flags.PVP_OVERWORLD
                        World.Environment.NETHER -> Flags.PVP_NETHER
                        World.Environment.THE_END -> Flags.PVP_END
                        else -> Flags.PVP_OVERWORLD
                    }
                )
            }
            is Monster -> island.isAllowed(User.getInstance(player), Flags.HURT_MONSTERS)
            else -> island.isAllowed(User.getInstance(player), Flags.HURT_ANIMALS)
        }
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        val island = BentoBox.getInstance().islandsManager.getIslandAt(location).orElse(null) ?: return true
        return island.isAllowed(User.getInstance(player), Flags.ITEM_PICKUP)
    }

    override fun getPluginName(): String {
        return "BentoBox"
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