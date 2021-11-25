package com.willfp.eco.internal.spigot.arrows

import com.willfp.eco.core.EcoPlugin
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent

class ArrowDataListener(
    private val plugin: EcoPlugin
) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onLaunch(event:ProjectileLaunchEvent) {
        val arrow = event.entity

        if (arrow !is Arrow) {
            return
        }

        if (arrow.shooter !is LivingEntity) {
            return
        }

        val entity = arrow.shooter as LivingEntity

        if (entity.equipment == null) {
            return
        }

        val item = entity.equipment!!.itemInMainHand

        if (item.type == Material.AIR || !item.hasItemMeta() || item.itemMeta == null) {
            return
        }

        arrow.setMetadata("shot-from", this.plugin.metadataValueFactory.create(item))
    }
}