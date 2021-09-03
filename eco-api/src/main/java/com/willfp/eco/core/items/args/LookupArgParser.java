package com.willfp.eco.core.items.args;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public interface LookupArgParser {
    /**
     * Parse the arguments.
     *
     * @param args The arguments.
     * @param meta The ItemMeta to modify.
     * @return The predicate test to apply to the modified item.
     */
    Predicate<ItemStack> parseArguments(@NotNull String[] args,
                                        @NotNull ItemMeta meta);
}
