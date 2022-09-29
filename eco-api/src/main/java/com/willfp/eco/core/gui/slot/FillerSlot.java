package com.willfp.eco.core.gui.slot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A filler slot is a slot that does nothing when clicked.
 * <p>
 * Useful for backgrounds.
 */
public class FillerSlot extends CustomSlot {
    /**
     * Create new filler slot.
     *
     * @param itemStack The ItemStack.
     */
    public FillerSlot(@NotNull final ItemStack itemStack) {
        init(
                Slot.builder(itemStack)
                        .build()
        );
    }
}
