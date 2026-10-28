package com.willfp.eco.internal.drops

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.DropQueuePushEvent
import com.willfp.eco.core.integrations.antigrief.AntigriefManager
import com.willfp.eco.util.TelekinesisUtils
import com.willfp.eco.util.giveExpAndApplyMending
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

    /**
     * Give the drops to the player, or drop them at the location.
     *
     * A player who has gone offline since the drops were queued gets them dropped at the location,
     * and drops in a world that has since been unloaded are discarded. The location is not changed,
     * so a queue pushed more than once drops in the same place every time.
     */
    override fun push() {
        val world = location.takeIf { it.isWorldLoaded }?.world ?: return

        if (!player.isOnline) {
            hasTelekinesis = false
        } else if (!hasTelekinesis) {
            hasTelekinesis = TelekinesisUtils.testPlayer(player)
        }

        if (hasTelekinesis && !AntigriefManager.canPickupItem(player, location)) {
            hasTelekinesis = false
        }

        val pushEvent = DropQueuePushEvent(player, items.toMutableList(), location, xp, hasTelekinesis)
        Bukkit.getServer().pluginManager.callEvent(pushEvent)

        if (pushEvent.isCancelled) {
            return
        }

        items.clear()
        items.addAll(pushEvent.items)
        xp = pushEvent.xp

        val dropLocation = location.clone().add(0.5, 0.5, 0.5)
        items.removeIf { itemStack: ItemStack -> itemStack.type == Material.AIR }
        if (hasTelekinesis) {
            if (!items.isEmpty()) {
                val leftover = player.inventory.addItem(*items.toTypedArray())
                for (drop in leftover.values) {
                    world.dropItem(dropLocation, drop!!).velocity = Vector()
                }
            }
            if (xp > 0)
                player.giveExpAndApplyMending(xp, true)
        } else {
            if (!items.isEmpty()) {
                for (drop in items) {
                    world.dropItem(dropLocation, drop).velocity = Vector()
                }
            }
            if (xp > 0) {
                val orb = world.spawnEntity(dropLocation, EntityType.EXPERIENCE_ORB) as ExperienceOrb
                orb.experience = xp
            }
        }
    }

    init {
        location = player.location
    }
}
