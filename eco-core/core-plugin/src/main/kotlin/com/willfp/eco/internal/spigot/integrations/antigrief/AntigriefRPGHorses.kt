package com.willfp.eco.internal.spigot.integrations.antigrief

import com.willfp.eco.core.integrations.antigrief.AntigriefIntegration
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.plugins.rpghorses.RPGHorsesMain

class AntigriefRPGHorses : AntigriefIntegration {
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
        val horse = RPGHorsesMain.getInstance().rpgHorseManager.getRPGHorse(victim)?: return true
        return horse.horseOwner.uuid.equals(player.uniqueId)
    }

    override fun canPickupItem(player: Player, location: Location): Boolean {
        return true
    }

    override fun getPluginName(): String {
        return "RPGHorses"
    }
}