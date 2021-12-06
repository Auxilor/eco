package com.willfp.eco.core.items.args;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Parse custom model data.
 *
 * @deprecated Moved to internals.
 */
@Deprecated(forRemoval = true)
public class CustomModelDataArgParser implements LookupArgParser {
    @Override
    public @Nullable Predicate<ItemStack> parseArguments(@NotNull final String[] args,
                                                         @NotNull final ItemMeta meta) {
        return null;
    }
}
