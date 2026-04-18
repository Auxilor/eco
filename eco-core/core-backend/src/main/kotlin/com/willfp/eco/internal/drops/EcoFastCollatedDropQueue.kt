package com.willfp.eco.internal.drops

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

class EcoFastCollatedDropQueue(player: Player) : EcoDropQueue(player) {
    override fun push() {
        val fetched = COLLATED_MAP[player]

        if (fetched == null) {
            COLLATED_MAP[player] = CollatedDrops(items, location, xp, hasTelekinesis)
        } else {
            fetched.addDrops(items)
            fetched.location = location
            fetched.addXp(xp)
            if (this.hasTelekinesis) {
                fetched.forceTelekinesis()
            }

            COLLATED_MAP[player] = fetched
        }
    }

    class CollatedDrops(
        val drops: MutableList<ItemStack>,
        var location: Location,
        var xp: Int,
        var telekinetic: Boolean
    ) {
        fun addDrops(toAdd: List<ItemStack>): CollatedDrops {
            drops.addAll(toAdd)
            return this
        }

        fun addXp(xp: Int): CollatedDrops {
            this.xp += xp
            return this
        }

        fun forceTelekinesis() {
            telekinetic = true
        }
    }

    companion object {
        val COLLATED_MAP: MutableMap<Player, CollatedDrops> = ConcurrentHashMap()
    }
}