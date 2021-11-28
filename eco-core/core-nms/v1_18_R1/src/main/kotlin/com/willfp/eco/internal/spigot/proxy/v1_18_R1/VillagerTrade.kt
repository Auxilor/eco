package com.willfp.eco.internal.spigot.proxy.v1_18_R1

import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxy.VillagerTradeProxy
import net.minecraft.world.item.trading.MerchantOffer
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchantRecipe
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
        getHandle(newRecipe).specialPriceDiff = getHandle(oldRecipe).specialPriceDiff
        return newRecipe
    }

    private fun getHandle(recipe: CraftMerchantRecipe): MerchantOffer {
        return handle[recipe] as MerchantOffer
    }

    init {
        handle.isAccessible = true
    }
}