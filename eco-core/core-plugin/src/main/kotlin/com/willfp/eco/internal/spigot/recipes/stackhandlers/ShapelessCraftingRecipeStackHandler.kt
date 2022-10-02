package com.willfp.eco.internal.spigot.recipes.stackhandlers

import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import com.willfp.eco.core.recipe.recipes.ShapelessCraftingRecipe
import com.willfp.eco.internal.spigot.recipes.StackedRecipeHandler
import org.bukkit.inventory.ItemStack

object ShapelessCraftingRecipeStackHandler :
    StackedRecipeHandler {
    override val recipeType = ShapelessCraftingRecipe::class.java
    override fun makeData(recipe: CraftingRecipe): Any {
        recipe as ShapelessCraftingRecipe
        return recipe.newTest()
    }

    override fun getPart(
        recipe: CraftingRecipe,
        position: Int,
        item: ItemStack,
        data: Any?
    ): TestableItem? {
        recipe as ShapelessCraftingRecipe
        data as ShapelessCraftingRecipe.RecipeTest

        return data.matchAndRemove(item)
    }
}
