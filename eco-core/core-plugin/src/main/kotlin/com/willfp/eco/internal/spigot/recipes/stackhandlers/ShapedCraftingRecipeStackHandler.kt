package com.willfp.eco.internal.spigot.recipes.stackhandlers

import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.recipe.recipes.CraftingRecipe
import com.willfp.eco.core.recipe.recipes.ShapedCraftingRecipe
import com.willfp.eco.internal.spigot.recipes.StackedRecipeHandler
import org.bukkit.inventory.ItemStack

object ShapedCraftingRecipeStackHandler : StackedRecipeHandler {
    override val recipeType = ShapedCraftingRecipe::class.java
    override fun makeData(recipe: CraftingRecipe): Any? = null

    override fun getPart(recipe: CraftingRecipe, position: Int, item: ItemStack, data: Any?): TestableItem? =
        recipe.parts[position]
}