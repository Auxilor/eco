package com.willfp.eco.internal.gui.slot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EcoFillerSlot extends EcoSlot {
    /**
     * Create new filler slot.
     *
     * @param itemStack The ItemStack.
     */
    public EcoFillerSlot(@NotNull final ItemStack itemStack) {
        super((player) -> itemStack, null, null, null, null, null);
    }
}
