package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for custom slot implementations.
 */
public abstract class ReactiveSlot implements Slot {
    /**
     * Create a new reactive slot.
     */
    protected ReactiveSlot() {

    }

    /**
     * Get the actual slot to be shown.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The slot.
     */
    @NotNull
    public abstract Slot getSlot(@NotNull final Player player,
                                 @NotNull final Menu menu);

    @Override
    public ItemStack getItemStack(@NotNull final Player player) {
        return new ItemStack(Material.STONE);
    }

    @Override
    public boolean isCaptive(@NotNull final Player player,
                             @NotNull final Menu menu) {
        return getSlot(player, menu).isCaptive(player, menu);
    }

    @Override
    public final Slot getActionableSlot(@NotNull final Player player,
                                        @NotNull final Menu menu) {
        return getSlot(player, menu);
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
