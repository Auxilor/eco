package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.core.items.Items
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.MaterialTestableItem
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem
import com.willfp.eco.core.recipe.parts.TestableStack
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener
import com.willfp.eco.internal.spigot.recipes.ShapedRecipeListener

class ComplexInEco : RecipeListener {
    override fun handle(event: GenericCraftEvent) {
        val craftingRecipe = Recipes.getRecipe(event.recipe.key)

        if (craftingRecipe !is ShapedCraftingRecipe) {
            return
        }

        if (ShapedRecipeListener.validators.any { it.validate(event) }) {
            return
        }

        for (i in 0..8) {
            val itemStack = event.inventory.matrix[i]
            val part = craftingRecipe.parts[i]
            when (part) {
                is MaterialTestableItem -> {
                    if (Items.isCustomItem(itemStack)) {
                        event.deny()
                    }
                }
                is ModifiedTestableItem -> {
                    if (part.handle is MaterialTestableItem) {
                        if (Items.isCustomItem(itemStack)) {
                            event.deny()
                        }
                    }
                }
                is TestableStack -> {
                    if (part.handle is MaterialTestableItem) {
                        if (Items.isCustomItem(itemStack)) {
                            event.deny()
                        }
                    }
                }
            }
        }
    }
}