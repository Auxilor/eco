package com.willfp.eco.internal.fast;

import com.willfp.eco.core.fast.FastItemStack;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class EcoFastItemStack<T> implements FastItemStack {
    @Getter
    private final T handle;

    @Getter
    private final ItemStack bukkit;

    protected EcoFastItemStack(@NotNull final T handle,
                               @NotNull final ItemStack bukkit) {
        this.handle = handle;
        this.bukkit = bukkit;
    }
}
