@file:Suppress("UnstableApiUsage")

package com.willfp.eco.internal.compat.modern.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.internal.recipes.AutocrafterPatch
import org.bukkit.event.EventHandler
import org.bukkit.event.block.CrafterCraftEvent

class AutocrafterPatchImpl: AutocrafterPatch {
    @EventHandler
    fun preventEcoRecipes(event: CrafterCraftEvent) {
        if (!EcoPlugin.getPluginNames().contains(event.recipe.key.namespace)) {
            return
        }

        event.isCancelled = true
    }
}
