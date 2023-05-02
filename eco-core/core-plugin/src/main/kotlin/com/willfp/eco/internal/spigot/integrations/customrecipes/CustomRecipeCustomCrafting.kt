package com.willfp.eco.internal.spigot.integrations.customrecipes

/*
class CustomRecipeCustomCrafting : RecipeValidator, Integration {
    override fun validate(event: GenericCraftEvent): Boolean {
        val player = event.inventory.viewers.getOrNull(0) as? Player ?: return false
        return CustomCrafting.inst().craftManager.has(player.uniqueId)
    }

    override fun getPluginName(): String {
        return "CustomCrafting"
    }
}

 */