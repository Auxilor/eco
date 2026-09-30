package com.willfp.eco.core.display;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Records what display changed on an item, so it can be reverted exactly.
 */
@ApiStatus.Internal
public interface DisplayRecorder {
    /**
     * Write a record of what display changed since a snapshot onto the item.
     *
     * @param itemStack    The displayed item.
     * @param snapshot     A copy of the item taken before display.
     * @param displayLines The positions of display lines in the lore.
     * @param recordLore   If the original lore must be recorded, because lines other than display
     *                     lines changed.
     */
    void record(@NotNull ItemStack itemStack,
                @NotNull ItemStack snapshot,
                @NotNull int[] displayLines,
                boolean recordLore);

    /**
     * Restore an item from its record, if it has a valid one.
     *
     * @param itemStack The item.
     * @return If the item was restored.
     */
    boolean restore(@NotNull ItemStack itemStack);
}
