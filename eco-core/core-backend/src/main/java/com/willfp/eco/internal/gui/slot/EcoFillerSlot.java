package com.willfp.eco.internal.gui.slot;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EcoFillerSlot extends EcoSlot {
    public EcoFillerSlot(@NotNull final ItemStack itemStack) {
        super((player) -> itemStack, null, null, null, null, null);
    }
}
