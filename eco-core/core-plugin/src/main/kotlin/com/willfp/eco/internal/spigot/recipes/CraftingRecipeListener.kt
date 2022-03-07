package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.Recipes
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent

class CraftingRecipeListener : Listener {
    @EventHandler
    fun preventLearningDisplayedRecipes(event: PlayerRecipeDiscoverEvent) {
        if (!EcoPlugin.getPluginNames().contains(event.recipe.namespace)) {
            return
        }
        if (event.recipe.key.contains("_displayed")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun processListeners(event: PrepareItemCraftEvent) {
        var recipe = event.recipe as? Keyed

        if (recipe == null) {
            val ecoRecipe = Recipes.getMatch(event.inventory.matrix)
            if (ecoRecipe != null) {
                recipe = Keyed { ecoRecipe.key }
            }
        }

        if (recipe == null) {
            return
        }

        for (listener in listeners) {
            listener.handle(WrappedPrepareItemCraftEvent(event, recipe))
        }
    }

    @EventHandler
    fun processListeners(event: CraftItemEvent) {
        if (event.recipe !is Keyed) {
            return
        }

        for (listener in listeners) {
            listener.handle(WrappedCraftItemEvent(event))
        }
    }

    companion object {
        val validators = mutableListOf<RecipeValidator>()
        private val listeners = mutableListOf<RecipeListener>()

        fun registerValidator(validator: RecipeValidator) {
            validators.add(validator)
        }

        fun registerListener(listener: RecipeListener) {
            listeners.add(listener)
        }
    }
}
