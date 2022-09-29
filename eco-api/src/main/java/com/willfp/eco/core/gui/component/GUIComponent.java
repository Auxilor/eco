package com.willfp.eco.core.gui.component;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A GUI Component is a 2-dimensional set of slots that can be
 * placed in a menu.
 */
public interface GUIComponent {
    /**
     * Get the amount of rows in the component.
     *
     * @return The rows.
     */
    int getRows();

    /**
     * Get the amount of columns in the component.
     *
     * @return The columns.
     */
    int getColumns();

    /**
     * Get the slot at a certain position in the component.
     * <p>
     * It's safe to assume to the row and column will always be in bounds.
     *
     * @param row    The row (1-indexed).
     * @param column The column (1-indexed).
     * @return The slot, or null if no slot at the location.
     */
    @Nullable
    default Slot getSlotAt(final int row,
                           final int column) {
        return null;
    }

    /**
     * Get the slot at a certain position in the component.
     * <p>
     * If your component doesn't use context data (player, menu),
     * then it will default to the raw slot.
     * <p>
     * It's safe to assume to the row and column will always be in bounds.
     *
     * @param row    The row (1-indexed).
     * @param column The column (1-indexed).
     * @param player The player.
     * @param menu   The menu.
     * @return The slot, or null if no slot at the location.
     */
    @Nullable
    default Slot getSlotAt(final int row,
                           final int column,
                           @NotNull final Player player,
                           @NotNull final Menu menu) {
        return getSlotAt(row, column);
    }
}
