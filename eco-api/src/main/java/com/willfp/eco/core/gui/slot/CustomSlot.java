package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public final @NotNull ItemStack getItemStack(@NotNull final Player player) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.getItemStack(player);
    }

    @Override
    public final boolean isCaptive(@NotNull final Player player,
                                   @NotNull final Menu menu) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.isCaptive(player, menu);
    }

    @Override
    public final boolean isAllowedCaptive(@NotNull final Player player,
                                          @NotNull final Menu menu,
                                          @Nullable final ItemStack itemStack) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.isAllowedCaptive(player, menu, itemStack);
    }

    @Override
    public final boolean isCaptiveFromEmpty() {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.isCaptiveFromEmpty();
    }

    @Override
    public final @NotNull Slot getActionableSlot(@NotNull final Player player,
                                                 @NotNull final Menu menu) {
        return delegate;
    }

    @Override
    public boolean shouldRenderOnClick(@NotNull final ClickType clickType) {
        if (delegate == null) {
            throw new IllegalStateException("Custom Slot was not initialized!");
        }

        return delegate.shouldRenderOnClick(clickType);
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
}
