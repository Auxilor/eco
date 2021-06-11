package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.core.display.Display;
import com.willfp.eco.proxy.proxies.VillagerTradeProxy;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchantRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class VillagerTrade implements VillagerTradeProxy {
    /**
     * Handle.
     */
    private final Field handle;

    /**
     * Create new Villager Trade.
     */
    public VillagerTrade() {
        try {
            handle = CraftMerchantRecipe.class.getDeclaredField("handle");
            handle.setAccessible(true);
            return;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error!");
    }

    @Override
    public MerchantRecipe displayTrade(@NotNull final MerchantRecipe recipe) {
        CraftMerchantRecipe oldRecipe = (CraftMerchantRecipe) recipe;

        CraftMerchantRecipe newRecipe = new CraftMerchantRecipe(
                Display.display(recipe.getResult().clone()),
                recipe.getUses(),
                recipe.getMaxUses(),
                recipe.hasExperienceReward(),
                recipe.getVillagerExperience(),
                recipe.getPriceMultiplier()
        );

        for (ItemStack ingredient : recipe.getIngredients()) {
            newRecipe.addIngredient(Display.display(ingredient.clone()));
        }

        getHandle(newRecipe).setSpecialPrice(getHandle(oldRecipe).getSpecialPrice());

        return newRecipe;
    }

    @NotNull
    private net.minecraft.world.item.trading.MerchantRecipe getHandle(@NotNull final CraftMerchantRecipe recipe) {
        try {
            return (net.minecraft.world.item.trading.MerchantRecipe) handle.get(recipe);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("Not CMR");
    }
}
