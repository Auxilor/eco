package com.willfp.eco.internal.spigot.recipes.listeners

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.recipe.Recipes
import com.willfp.eco.internal.spigot.recipes.CraftingRecipeListener
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeListener
import org.bukkit.entity.Player

object ComplexInComplex : RecipeListener {
    override fun handle(event: GenericCraftEvent) {
        val recipe = event.recipe

        if (!EcoPlugin.getPluginNames().contains(recipe.key.namespace)) {
            return
        }

        val player = event.inventory.viewers.getOrNull(0) as? Player ?: return

        val matrix = event.inventory.matrix

        if (CraftingRecipeListener.validators.any { it.validate(event) }) {
            return
        }

        val matched = Recipes.getMatch(matrix)

        if (matched == null) {
            event.deny()
            return
        }

        if (matched.test(matrix)) {
            if (matched.permission != null) {
                if (player.hasPermission(matched.permission!!)) {
                    event.allow(matched)
                } else {
                    event.deny()
                }
            } else {
                event.allow(matched)
            }
        } else {
            event.deny()
        }
    }
}
