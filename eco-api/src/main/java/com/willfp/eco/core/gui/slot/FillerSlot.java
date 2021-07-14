package com.willfp.eco.core.gui.slot;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class FillerSlot implements Slot {
    /**
     * The ItemStack.
     */
    @Getter
    private final ItemStack itemStack;

    /**
     * Create new filler slot.
     *
     * @param itemStack The ItemStack.
     */
    public FillerSlot(@NotNull final ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getItemStack(@NotNull final Player player) {
        return itemStack;
    }
}
