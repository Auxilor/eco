package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.Items
import com.willfp.eco.internal.spigot.recipes.CraftingRecipeListener
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.RecipeChoice
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

object ComplexInVanilla : RecipeListener {
    override fun handle(event: GenericCraftEvent) {
        if (EcoPlugin.getPluginNames().contains(event.recipe.key.namespace)) {
            return
        }

        // Recipes from other plugins ask for their own custom items on purpose, so
        // only vanilla recipes (which match by material alone) need protecting.
        if (event.recipe.key.namespace != NamespacedKey.MINECRAFT) {
            return
        }

        if (CraftingRecipeListener.validators.any { it.validate(event) }) {
            return
        }

        val exactChoices = exactChoicesOf(event.recipe as? Recipe)

        for (itemStack in event.inventory.matrix) {
            val item = itemStack ?: continue

            if (item.type == Material.SHIELD) {
                continue
            }

            if (!Items.isCustomItem(item)) {
                continue
            }

            // If the recipe explicitly asks for this exact item, it isn't being
            // passed off as its base material, so let it through.
            if (exactChoices.any { it.test(item) }) {
                continue
            }

            event.deny()
        }
    }

    private fun exactChoicesOf(recipe: Recipe?): List<RecipeChoice.ExactChoice> {
        val choices: Collection<RecipeChoice?> = when (recipe) {
            is ShapedRecipe -> recipe.choiceMap.values
            is ShapelessRecipe -> recipe.choiceList
            else -> emptyList()
        }

        return choices.filterIsInstance<RecipeChoice.ExactChoice>()
    }
}
