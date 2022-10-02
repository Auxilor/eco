package com.willfp.eco.util;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.tuples.Pair;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utilities / API methods for menus.
 */
public final class MenuUtils {
    /**
     * Convert 0-53 slot to row and column pair.
     *
     * @param slot The slot.
     * @return The pair of row and columns.
     */
    @NotNull
    public static Pair<Integer, Integer> convertSlotToRowColumn(final int slot) {
        return convertSlotToRowColumn(slot, 9);
    }

    /**
     * Convert row and column to 0-53 slot.
     *
     * @param row    The row.
     * @param column The column.
     * @return The slot.
     */
    public static int rowColumnToSlot(final int row, final int column) {
        return rowColumnToSlot(row, column, 9);
    }

    /**
     * Convert 0-53 slot to row and column pair.
     *
     * @param slot    The slot.
     * @param columns The columns.
     * @return The pair of row and columns.
     */
    @NotNull
    public static Pair<Integer, Integer> convertSlotToRowColumn(final int slot,
                                                                final int columns) {
        int row = Math.floorDiv(slot, columns);
        int column = slot - row * columns;
        return new Pair<>(row + 1, column + 1);
    }

    /**
     * Convert row and column to 0-53 slot.
     *
     * @param row     The row.
     * @param column  The column.
     * @param columns The columns in the menu.
     * @return The slot.
     */
    public static int rowColumnToSlot(final int row,
                                      final int column,
                                      final int columns) {
        return (column - 1) + ((row - 1) * columns);
    }

    /**
     * Get a player's open menu.
     *
     * @param player The player.
     * @return The menu, or null if none open.
     */
    @Nullable
    public static Menu getOpenMenu(@NotNull final Player player) {
        return Eco.get().getOpenMenu(player);
    }

    private MenuUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
