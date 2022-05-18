package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

internal class EntityDeathByEntityBuilder {
    var victim: LivingEntity? = null
    var damager: Entity? = null
    var deathEvent: EntityDeathEvent? = null
    var drops: List<ItemStack>? = null
    var xp = 0

    fun push() {
        val event = EntityDeathByEntityEvent(victim!!, damager!!, drops!!, xp, deathEvent!!)
        Bukkit.getPluginManager().callEvent(event)
    }
}