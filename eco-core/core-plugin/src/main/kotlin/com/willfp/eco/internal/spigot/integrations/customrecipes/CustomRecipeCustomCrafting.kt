package com.willfp.eco.internal.spigot.integrations.customrecipes

import com.willfp.eco.core.integrations.Integration
import com.willfp.eco.internal.spigot.recipes.GenericCraftEvent
import com.willfp.eco.internal.spigot.recipes.RecipeValidator
import me.wolfyscript.customcrafting.CustomCrafting
import org.bukkit.entity.Player

class CustomRecipeCustomCrafting : RecipeValidator, Integration {
    override fun validate(event: GenericCraftEvent): Boolean {
        val player = event.inventory.viewers.getOrNull(0) as? Player ?: return false
        return CustomCrafting.inst().craftManager.has(player.uniqueId)
    }

    override fun getPluginName(): String {
        return "CustomCrafting"
    }
}