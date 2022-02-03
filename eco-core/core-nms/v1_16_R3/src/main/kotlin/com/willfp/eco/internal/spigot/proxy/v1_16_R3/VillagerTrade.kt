package com.willfp.eco.internal.spigot.proxy.v1_16_R3

import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxy.VillagerTradeProxy
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftMerchantRecipe
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe
import java.lang.reflect.Field

class VillagerTrade : VillagerTradeProxy {
    private val handle: Field = CraftMerchantRecipe::class.java.getDeclaredField("handle")

    override fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe {
        val oldRecipe = recipe as CraftMerchantRecipe
        val newRecipe = CraftMerchantRecipe(
            Display.display(recipe.getResult().clone(), player),
            recipe.getUses(),
            recipe.getMaxUses(),
            recipe.hasExperienceReward(),
            recipe.getVillagerExperience(),
            recipe.getPriceMultiplier()
        )
        for (ingredient in recipe.getIngredients()) {
            newRecipe.addIngredient(Display.display(ingredient.clone(), player))
        }
        getHandle(newRecipe).specialPrice = getHandle(oldRecipe).specialPrice
        return newRecipe
    }

    private fun getHandle(recipe: CraftMerchantRecipe): net.minecraft.server.v1_16_R3.MerchantRecipe {
        return handle[recipe] as net.minecraft.server.v1_16_R3.MerchantRecipe
    }

    init {
        handle.isAccessible = true
    }
}