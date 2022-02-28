package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.core.recipe.parts.GroupedTestableItems
import com.willfp.eco.core.recipe.parts.MaterialTestableItem
import com.willfp.eco.core.recipe.parts.ModifiedTestableItem
import com.willfp.eco.core.recipe.parts.TestableStack
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener
import com.willfp.eco.internal.spigot.recipes.ShapedRecipeListener
import org.bukkit.inventory.ItemStack

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
            if (part.isCustomWhenShouldNotBe(itemStack)) {
                event.deny()
            }
        }
    }
}

private fun TestableItem.isCustomWhenShouldNotBe(itemStack: ItemStack): Boolean {
    when (this) {
        is MaterialTestableItem -> {
            if (Items.isCustomItem(itemStack)) {
                return true
            }
        }
        is ModifiedTestableItem -> {
            if (this.handle is MaterialTestableItem) {
                if (Items.isCustomItem(itemStack)) {
                    return true
                }
            }
        }
        is TestableStack -> {
            if (this.handle is MaterialTestableItem) {
                if (Items.isCustomItem(itemStack)) {
                    return true
                }
            }
        }
        is GroupedTestableItems -> {
            // This will fail if and only if there is a complex item grouped with a simple item of the same type
            if (this.children.any { it.isCustomWhenShouldNotBe(itemStack) && it.matches(itemStack) }) {
                return true
            }
        }
    }

    return false
}