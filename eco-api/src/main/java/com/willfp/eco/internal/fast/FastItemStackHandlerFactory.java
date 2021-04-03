package com.willfp.eco.internal.fast;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface FastItemStackHandlerFactory {
    /**
     * Create new FastItemStackHandler.
     *
     * @param itemStack The ItemStack to handle.
     * @return The handler.
     */
    AbstractFastItemStackHandler create(@NotNull ItemStack itemStack);
}
