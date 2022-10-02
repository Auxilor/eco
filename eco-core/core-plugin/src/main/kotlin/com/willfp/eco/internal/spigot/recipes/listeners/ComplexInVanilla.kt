package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.Items
import com.willfp.eco.internal.spigot.recipes.CraftingRecipeListener
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener

object ComplexInVanilla : RecipeListener {
    override fun handle(event: GenericCraftEvent) {
        if (EcoPlugin.getPluginNames().contains(event.recipe.key.namespace)) {
            return
        }

        if (CraftingRecipeListener.validators.any { it.validate(event) }) {
            return
        }

        for (itemStack in event.inventory.matrix) {
            if (Items.isCustomItem(itemStack)) {
                event.deny()
            }
        }
    }
}
