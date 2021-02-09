package com.willfp.eco.proxy.v1_16_R1;

import com.willfp.eco.proxy.proxies.VillagerTradeProxy;
import com.willfp.eco.util.display.Display;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R1.inventory.CraftMerchantRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class VillagerTrade implements VillagerTradeProxy {
    @Override
    public void displayTrade(@NotNull final MerchantRecipe merchantRecipe) {
        try {
            // Bukkit MerchantRecipe result
            Field fResult = MerchantRecipe.class.getDeclaredField("result");
            fResult.setAccessible(true);
            ItemStack result = merchantRecipe.getResult();
            Display.display(result);
            fResult.set(merchantRecipe, result);

            // Get NMS MerchantRecipe from CraftMerchantRecipe
            Field fHandle = CraftMerchantRecipe.class.getDeclaredField("handle");
            fHandle.setAccessible(true);
            net.minecraft.server.v1_16_R1.MerchantRecipe handle = (net.minecraft.server.v1_16_R1.MerchantRecipe) fHandle.get(merchantRecipe); // NMS Recipe

            Field fSelling = net.minecraft.server.v1_16_R1.MerchantRecipe.class.getDeclaredField("sellingItem");
            fSelling.setAccessible(true);

            ItemStack selling = CraftItemStack.asBukkitCopy(handle.sellingItem);
            Display.display(selling);

            fSelling.set(handle, CraftItemStack.asNMSCopy(selling));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
