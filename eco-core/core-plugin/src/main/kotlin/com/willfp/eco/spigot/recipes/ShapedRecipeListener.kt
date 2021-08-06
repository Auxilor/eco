package com.willfp.eco.spigot.recipes

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.PluginDependent
import com.willfp.eco.core.recipe.Recipes
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe

class ShapedRecipeListener(
        plugin: EcoPlugin
): PluginDependent<EcoPlugin>(plugin), Listener {
    @EventHandler
    fun complexRecipeListener(event: PrepareItemCraftEvent) {
        val recipe = event.recipe

        if (recipe !is ShapedRecipe) {
            return
        }

        if (!EcoPlugin.getPluginNames().contains(recipe.key.namespace)) {
            return
        }

        val matrix = event.inventory.matrix
        val matched = Recipes.getMatch(matrix)

        if (matched == null) {
            event.inventory.result = ItemStack(Material.AIR)
            return
        }

        if (matched.test(matrix)) {
            event.inventory.result = matched.output
        } else {
            event.inventory.result = ItemStack(Material.AIR)
        }
    }

    @EventHandler
    fun complexRecipeListener(event: CraftItemEvent) {
        val recipe = event.recipe

        if (recipe !is ShapedRecipe) {
            return
        }

        if (!EcoPlugin.getPluginNames().contains(recipe.key.namespace)) {
            return
        }

        val matrix = event.inventory.matrix
        val matched = Recipes.getMatch(matrix)

        if (matched == null) {
            event.inventory.result = ItemStack(Material.AIR)
            event.result = Event.Result.DENY
            event.isCancelled = true
            return
        }

        if (matched.test(matrix)) {
            event.inventory.result = matched.output
        } else {
            event.inventory.result = ItemStack(Material.AIR)
            event.result = Event.Result.DENY
            event.isCancelled = true
        }
    }
}