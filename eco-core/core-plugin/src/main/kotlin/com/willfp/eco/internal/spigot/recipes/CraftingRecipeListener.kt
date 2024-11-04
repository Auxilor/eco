package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.util.namespacedKeyOf
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent

class CraftingRecipeListener(val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun handleDisplayedRecipeUnlocksPre1213(event: PlayerRecipeDiscoverEvent) {
        if (Prerequisite.HAS_1_21_3.isMet) {
            return
        }

        if (!EcoPlugin.getPluginNames().contains(event.recipe.namespace)) {
            return
        }

        if (event.recipe.key.contains("_displayed")) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun handleDisplayedRecipeUnlocks1213(event: PlayerRecipeDiscoverEvent) {
        if (!Prerequisite.HAS_1_21_3.isMet) {
            return
        }

        if (!EcoPlugin.getPluginNames().contains(event.recipe.namespace)) {
            return
        }

        if (!event.recipe.key.contains("_displayed")) {
            event.isCancelled = true

            val player = event.player
            player.discoverRecipe(namespacedKeyOf(
                event.recipe.namespace,
                event.recipe.key + "_displayed"
            ))
        }
    }

    @EventHandler
    fun processListeners(event: PrepareItemCraftEvent) {
        handlePrepare(event)

        if (plugin.configYml.getBool("enforce-preparing-recipes")) {
            plugin.scheduler.runLater(1) {
                handlePrepare(event)
            }
        }
    }

    private fun handlePrepare(event: PrepareItemCraftEvent) {
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
