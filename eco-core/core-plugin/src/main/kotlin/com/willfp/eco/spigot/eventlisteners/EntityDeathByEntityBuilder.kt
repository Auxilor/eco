package com.willfp.eco.spigot.eventlisteners

import com.willfp.eco.core.events.EntityDeathByEntityEvent
import org.apache.commons.lang.Validate
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

        Validate.notNull(victim)
        Validate.notNull(damager)
        Validate.notNull(drops)
        Validate.notNull(deathEvent)
        val event = EntityDeathByEntityEvent(victim!!, damager!!, drops!!, xp, deathEvent!!)
        Bukkit.getPluginManager().callEvent(event)
    }
}