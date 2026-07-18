package com.willfp.eco.internal.spigot.proxy.common.recipes

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe

object RecipeManager {
    fun removeRecipeNoResend(recipeKey: NamespacedKey): Boolean =
        Bukkit.removeRecipe(recipeKey, false)

    fun addRecipeNoResend(recipe: Recipe): Boolean =
        Bukkit.addRecipe(recipe, false)

    fun reloadRecipes() {
        Bukkit.updateRecipes()
    }
}