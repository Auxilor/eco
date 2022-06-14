package com.willfp.eco.core.items;

import com.willfp.eco.core.Eco;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * API to handle SNBT conversion.
 */
@ApiStatus.Internal
@Eco.HandlerComponent
public interface SNBTHandler {
    /**
     * Get item from SNBT.
     *
     * @param snbt The NBT string.
     * @return The ItemStack, or null if invalid.
     */
    @Nullable
    ItemStack fromSNBT(@NotNull String snbt);

    /**
     * Convert item to SNBT.
     *
     * @param itemStack The item.
     * @return The NBT string.
     */
    @NotNull
    String toSNBT(@NotNull ItemStack itemStack);
}
