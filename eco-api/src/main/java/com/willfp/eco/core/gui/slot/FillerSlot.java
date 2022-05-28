package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.items.TestableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A filler slot is a slot that does nothing when clicked.
 * <p>
 * Useful for backgrounds.
 */
public class FillerSlot implements Slot {
    /**
     * The ItemStack.
     */
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

    @Override
    public boolean isCaptive() {
        return false;
    }

    /**
     * Get the ItemStack.
     *
     * @return The ItemStack.
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
