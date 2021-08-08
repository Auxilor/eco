package com.willfp.eco.internal.drops.impl

import com.willfp.eco.core.drops.InternalDropQueue
import com.willfp.eco.util.TelekinesisUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

open class EcoDropQueue(player: Player) : InternalDropQueue {
    val items: MutableList<ItemStack>
    var xp: Int
    val player: Player
    var loc: Location

    private var hasTelekinesis = false
    override fun addItem(item: ItemStack): InternalDropQueue {
        items.add(item)
        return this
    }

    override fun addItems(itemStacks: Collection<ItemStack>): InternalDropQueue {
        items.addAll(itemStacks)
        return this
    }

    override fun addXP(amount: Int): InternalDropQueue {
        xp += amount
        return this
    }

    override fun setLocation(location: Location): InternalDropQueue {
        loc = location
        return this
    }

    override fun forceTelekinesis(): InternalDropQueue {
        hasTelekinesis = true
        return this
    }

    override fun push() {
        if (!hasTelekinesis) {
            hasTelekinesis = TelekinesisUtils.testPlayer(player)
        }
        val world = loc.world!!
        loc = loc.add(0.5, 0.5, 0.5)
        items.removeIf { itemStack: ItemStack -> itemStack.type == Material.AIR }
        if (items.isEmpty()) {
            return
        }
        if (hasTelekinesis) {
            val leftover = player.inventory.addItem(*items.toTypedArray())
            for (drop in leftover.values) {
                world.dropItem(loc, drop!!).velocity = Vector()
            }
            if (xp > 0) {
                val event = PlayerExpChangeEvent(player, xp)
                Bukkit.getPluginManager().callEvent(event)
                val orb =
                    world.spawnEntity(player.location.add(0.0, 0.2, 0.0), EntityType.EXPERIENCE_ORB) as ExperienceOrb
                orb.velocity = Vector(0, 0, 0)
                orb.experience = event.amount
            }
        } else {
            for (drop in items) {
                world.dropItem(loc, drop).velocity = Vector()
            }
            if (xp > 0) {
                val orb = world.spawnEntity(loc, EntityType.EXPERIENCE_ORB) as ExperienceOrb
                orb.experience = xp
            }
        }
    }

    init {
        items = mutableListOf()
        xp = 0
        this.player = player
        loc = player.location
    }
}
