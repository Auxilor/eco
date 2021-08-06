package com.willfp.eco.proxy.v1_17_R1

import com.willfp.eco.core.display.Display
import com.willfp.eco.proxy.VillagerTradeProxy
import net.minecraft.world.item.trading.MerchantOffer
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchantRecipe
import org.bukkit.entity.Player
import org.bukkit.inventory.MerchantRecipe
import java.lang.reflect.Field

class VillagerTrade : VillagerTradeProxy {
    private lateinit var handle: Field

    override fun displayTrade(
        recipe: MerchantRecipe,
        player: Player
    ): MerchantRecipe {
        val oldRecipe = recipe as CraftMerchantRecipe
        val newRecipe = CraftMerchantRecipe(
            Display.display(recipe.getResult().clone()),
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
        try {
            return handle[recipe] as MerchantOffer
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        throw IllegalArgumentException("Not CMR")
    }

    init {
        try {
            handle = CraftMerchantRecipe::class.java.getDeclaredField("handle")
            handle.isAccessible = true
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
        throw RuntimeException("Error!")
    }
}