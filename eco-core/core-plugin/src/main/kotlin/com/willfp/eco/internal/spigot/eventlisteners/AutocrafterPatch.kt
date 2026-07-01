@file:Suppress("UnstableApiUsage")

package com.willfp.eco.internal.spigot.eventlisteners

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.Recipes
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent

object AutocrafterPatch : Listener {
    @EventHandler
    fun preventEcoRecipes(event: CrafterCraftEvent) {
        val key = event.recipe.key
        if (!EcoPlugin.getPluginNames().contains(key.namespace)) {
            return
        }

        // Strip the "_crafter" suffix used by support-crafter Bukkit recipes
        // so we can look up the underlying eco CraftingRecipe.
        val lookupKey = if (key.key.endsWith("_crafter")) {
            NamespacedKey(key.namespace, key.key.removeSuffix("_crafter"))
        } else {
            key
        }
        val recipe = Recipes.getRecipe(lookupKey)
        if (recipe != null && recipe.isSupportCrafter) {
            return
        }

        event.isCancelled = true
    }
}
