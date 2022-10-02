package com.willfp.eco.internal.drops

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.DropQueuePushEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.TelekinesisUtils
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

open class EcoDropQueue(val player: Player) : DropQueue() {
    val items = mutableListOf<ItemStack>()
    var xp: Int = 0
    var location: Location
    var hasTelekinesis = false

    override fun addItem(item: ItemStack): DropQueue {
        items.add(item)
        return this
    }

    override fun addItems(itemStacks: Collection<ItemStack>): DropQueue {
        items.addAll(itemStacks)
        return this
    }

    override fun addXP(amount: Int): DropQueue {
        xp += amount
        return this
    }

    override fun setLocation(location: Location): DropQueue {
        this.location = location
        return this
    }

    override fun forceTelekinesis(): DropQueue {
        hasTelekinesis = true
        return this
    }

    override fun push() {
        if (!hasTelekinesis) {
            hasTelekinesis = TelekinesisUtils.testPlayer(player)
        }

        if (hasTelekinesis && !AntigriefManager.canPickupItem(player, location)) {
            hasTelekinesis = false
        }

        val pushEvent = DropQueuePushEvent(player, items, location, xp, hasTelekinesis)
        Bukkit.getServer().pluginManager.callEvent(pushEvent)

        if (pushEvent.isCancelled) {
            return
        }

        val world = location.world!!
        location = location.add(0.5, 0.5, 0.5)
        items.removeIf { itemStack: ItemStack -> itemStack.type == Material.AIR }
        if (items.isEmpty()) {
            return
        }
        if (hasTelekinesis) {
            val leftover = player.inventory.addItem(*items.toTypedArray())
            for (drop in leftover.values) {
                world.dropItem(location, drop!!).velocity = Vector()
            }
            if (xp > 0) {
                val orb =
                    world.spawnEntity(player.location.add(0.0, 0.2, 0.0), EntityType.EXPERIENCE_ORB) as ExperienceOrb
                orb.velocity = Vector(0, 0, 0)
                orb.experience = xp
            }
        } else {
            for (drop in items) {
                world.dropItem(location, drop).velocity = Vector()
            }
            if (xp > 0) {
                val orb = world.spawnEntity(location, EntityType.EXPERIENCE_ORB) as ExperienceOrb
                orb.experience = xp
            }
        }
    }

    init {
        location = player.location
    }
}
