package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for custom slot implementations.
 */
public abstract class CustomSlot implements Slot {
    /**
     * The internal slot to delegate to.
     */
    private Slot delegate = null;

    /**
     * Create a new custom slot.
     */
    protected CustomSlot() {

    }

    /**
     * Initialize the slot with the delegate.
     *
     * @param slot The slot to delegate to.
     */
    protected void init(@NotNull final Slot slot) {
        this.delegate = slot;
    }

    @Override
    public ItemStack getItemStack(@NotNull final Player player) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.getItemStack(player);
    }

    @Override
    public boolean isCaptive(@NotNull final Player player,
                             @NotNull final Menu menu) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.isCaptive(player, menu);
    }

    @Override
    public boolean isCaptiveFromEmpty() {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.isCaptiveFromEmpty();
    }

    @Override
    public final Slot getActionableSlot(@NotNull final Player player,
                                        @NotNull final Menu menu) {
        return delegate;
    }

    @Override
    public final int getRows() {
        return Slot.super.getRows();
    }

    @Override
    public final int getColumns() {
        return Slot.super.getColumns();
    }

    @Override
    public final Slot getSlotAt(int row, int column) {
        return Slot.super.getSlotAt(row, column);
    }

    /**
     * Get the delegate slot.
     * <p>
     * This is not required to add the slot to a menu, but is instead used internally.
     *
     * @return The slot.
     * @deprecated Replaced with {@link Slot#getActionableSlot(Player, Menu)}
     */
    @Deprecated(since = "6.43.0", forRemoval = true)
    public Slot getDelegate() {
        return this.delegate;
    }
}
