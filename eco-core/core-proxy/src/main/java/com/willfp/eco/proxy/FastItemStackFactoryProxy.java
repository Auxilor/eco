package com.willfp.eco.proxy;

import com.willfp.eco.core.fast.FastItemStack;
import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface FastItemStackFactoryProxy extends AbstractProxy {
    FastItemStack create(@NotNull ItemStack itemStack);
}
