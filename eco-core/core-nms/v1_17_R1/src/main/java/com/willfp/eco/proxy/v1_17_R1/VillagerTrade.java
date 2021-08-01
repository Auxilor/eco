package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.core.display.Display;
import com.willfp.eco.proxy.VillagerTradeProxy;
import net.minecraft.world.item.trading.MerchantOffer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftMerchantRecipe;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class VillagerTrade implements VillagerTradeProxy {
    private final Field handle;

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
    public MerchantRecipe displayTrade(@NotNull final MerchantRecipe recipe,
                                       @NotNull final Player player) {
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
            newRecipe.addIngredient(Display.display(ingredient.clone(), player));
        }

        getHandle(newRecipe).setSpecialPriceDiff(getHandle(oldRecipe).getSpecialPriceDiff());

        return newRecipe;
    }

    @NotNull
    private MerchantOffer getHandle(@NotNull final CraftMerchantRecipe recipe) {
        try {
            return (MerchantOffer) handle.get(recipe);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        throw new IllegalArgumentException("Not CMR");
    }
}
