package com.willfp.eco.internal.spigot.integrations.customrecipes

import com.willfp.eco.internal.spigot.recipes.RecipeValidator
import me.wolfyscript.customcrafting.CustomCrafting
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent

class CustomRecipeCustomCrafting: RecipeValidator {
    override fun validate(event: CraftItemEvent): Boolean {
        if (event.inventory.viewers.isEmpty()) {
            return false
        }
        val player = event.inventory.viewers[0]
        return CustomCrafting.inst().craftManager.has(player.uniqueId)
    }

    override fun validate(event: PrepareItemCraftEvent): Boolean {
        if (event.inventory.viewers.isEmpty()) {
            return false
        }
        val player = event.inventory.viewers[0]
        return CustomCrafting.inst().craftManager.has(player.uniqueId)
    }
}