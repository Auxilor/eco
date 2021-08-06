package com.willfp.eco.spigot.arrows

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent

class ArrowDataListener(
    plugin: EcoPlugin
) : PluginDependent<EcoPlugin>(plugin), Listener {

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