package com.willfp.eco.proxy.v1_17_R1;

import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.proxy.FastItemStackFactoryProxy;
import com.willfp.eco.proxy.v1_17_R1.fast.EcoFastItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class FastItemStackFactory implements FastItemStackFactoryProxy {
    @Override
    public FastItemStack create(@NotNull final ItemStack itemStack) {
        return new EcoFastItemStack(itemStack);
    }
}
