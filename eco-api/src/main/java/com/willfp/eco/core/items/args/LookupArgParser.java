package com.willfp.eco.core.items.args;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * An argument parser should generate the predicate as well
 * as modify the ItemMeta for {@link TestableItem#getItem()}.
 */
public interface LookupArgParser {
    /**
     * Parse the arguments.
     *
     * @param args The arguments.
     * @param meta The ItemMeta to modify.
     * @return The predicate test to apply to the modified item.
     */
    @Nullable Predicate<ItemStack> parseArguments(@NotNull String[] args,
                                                  @NotNull ItemMeta meta);

    /**
     * Serialize the item back to a string.
     *
     * @param meta The ItemMeta.
     * @return The string, or null if not required.
     */
    default @Nullable String serializeBack(@NotNull final ItemMeta meta) {
        return null;
    }
}
