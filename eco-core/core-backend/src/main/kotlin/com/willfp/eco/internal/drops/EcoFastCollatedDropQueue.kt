package com.willfp.eco.internal.drops

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class EcoFastCollatedDropQueue(player: Player) : EcoDropQueue(player) {
    override fun push() {
        val uuid = player.uniqueId
        val fetched = COLLATED_MAP[uuid]

        if (fetched == null) {
            COLLATED_MAP[uuid] = CollatedDrops(items, location, xp, hasTelekinesis)
        } else {
            fetched.addDrops(items)
            fetched.location = location
            fetched.addXp(xp)
            if (this.hasTelekinesis) {
                fetched.forceTelekinesis()
            }

            COLLATED_MAP[uuid] = fetched
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
        val COLLATED_MAP: MutableMap<UUID, CollatedDrops> = ConcurrentHashMap()
    }
}