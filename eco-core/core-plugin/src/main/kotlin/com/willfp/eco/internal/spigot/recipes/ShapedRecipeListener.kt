package com.willfp.eco.internal.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.TestableStack
import org.bukkit.Keyed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.player.PlayerRecipeDiscoverEvent
import org.bukkit.inventory.ShapedRecipe

class ShapedRecipeListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun stackedRecipeListener(event: CraftItemEvent) {
        val recipe = event.recipe as? ShapedRecipe ?: return

        if (!EcoPlugin.getPluginNames().contains(recipe.key.namespace)) {
            return
        }

        val matrix = event.inventory.matrix

        val wrapped = WrappedCraftItemEvent(event)

        if (validators.any { it.validate(wrapped) }) {
            return
        }

        val matched = Recipes.getMatch(matrix)

        if (matched == null) {
            wrapped.deny()
            return
        }

        var isStackedRecipe = false
        var upperBound = 64
        for (i in 0..8) {
            val inMatrix = event.inventory.matrix.getOrNull(i)
            val inRecipe = matched.parts[i]

            if (inRecipe is TestableStack) {
                val max = Math.floorDiv(inMatrix!!.amount, inRecipe.amount)
                if (max < upperBound) {
                    upperBound = max
                }
                isStackedRecipe = true
            } else if (inMatrix != null) {
                val max = inMatrix.amount
                if (max < upperBound) {
                    upperBound = max
                }
            }
        }

        if (!isStackedRecipe) {
            return
        }

        val toGivePerRecipe = event.recipe.result.amount
        val maxStackSize = event.recipe.result.maxStackSize
        while (toGivePerRecipe * upperBound > maxStackSize) {
            upperBound--
        }

        for (i in 0..8) {
            val inMatrix = event.inventory.matrix[i]
            val inRecipe = matched.parts[i]

            if (inRecipe is TestableStack) {
                if (event.isShiftClick) {
                    var amount = inMatrix.amount + 1
                    for (j in 0..upperBound) {
                        amount -= inRecipe.amount
                    }
                    inMatrix.amount = amount
                } else {
                    inMatrix.amount = inMatrix.amount - (inRecipe.amount - 1)
                }
            }
        }

        if (event.isShiftClick) {
            val result = event.inventory.result ?: return

            result.amount = result.amount * upperBound
            event.inventory.result = result
        }
    }

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
        if (event.recipe !is Keyed) {
            return
        }

        for (listener in listeners) {
            listener.handle(WrappedPrepareItemCraftEvent(event))
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