@file:Suppress("UnstableApiUsage")

package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.EcoPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent

object AutocrafterPatch : Listener {
    @EventHandler
    fun preventEcoRecipes(event: CrafterCraftEvent) {
        if (!EcoPlugin.getPluginNames().contains(event.recipe.key.namespace)) {
            return
        }

        event.isCancelled = true
    }
}
