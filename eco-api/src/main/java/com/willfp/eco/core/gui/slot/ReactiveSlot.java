package com.willfp.eco.core.gui.slot;

import com.willfp.eco.core.gui.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for slots that resolve to a different slot depending on
 * the player and the menu they are viewing.
 * <p>
 * Unlike {@link CustomSlot}, which delegates to a single fixed slot, the
 * delegate is looked up on every call through {@link #getSlot(Player, Menu)}.
 */
public abstract class ReactiveSlot implements Slot {
    /**
     * Create a new reactive slot.
     */
    protected ReactiveSlot() {

    }

    /**
     * Get the actual slot to be shown to a player.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The slot to delegate to.
     */
    @NotNull
    public abstract Slot getSlot(@NotNull final Player player,
                                 @NotNull final Menu menu);

    @Override
    public @NotNull ItemStack getItemStack(@NotNull final Player player) {
        return new ItemStack(Material.AIR);
    }

    @Override
    public final boolean isCaptive(@NotNull final Player player,
                                   @NotNull final Menu menu) {
        return getSlot(player, menu).isCaptive(player, menu);
    }

    @Override
    public final boolean isAllowedCaptive(@NotNull final Player player,
                                          @NotNull final Menu menu,
                                          @Nullable final ItemStack itemStack) {
        return getSlot(player, menu).isAllowedCaptive(player, menu, itemStack);
    }

    @Override
    public final @NotNull Slot getActionableSlot(@NotNull final Player player,
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
