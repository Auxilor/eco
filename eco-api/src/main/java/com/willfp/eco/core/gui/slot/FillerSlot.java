package com.willfp.eco.core.gui.slot;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A filler slot is a slot that does nothing when clicked.
 * <p>
 * Useful for backgrounds.
 */
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
