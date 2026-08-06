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
     * Convert a zero-based inventory slot to a one-based row and column pair.
     * <p>
     * Assumes a menu nine columns wide, so slot 0 maps to row 1, column 1 and slot 53 maps to
     * row 6, column 9.
     *
     * @param slot The zero-based slot.
     * @return The pair of one-based row and one-based column.
     */
    @NotNull
    public static Pair<Integer, Integer> convertSlotToRowColumn(final int slot) {
        return convertSlotToRowColumn(slot, 9);
    }

    /**
     * Convert a one-based row and column to a zero-based inventory slot.
     * <p>
     * Assumes a menu nine columns wide, so row 1, column 1 maps to slot 0 and row 6, column 9
     * maps to slot 53.
     *
     * @param row    The one-based row.
     * @param column The one-based column.
     * @return The zero-based slot.
     */
    public static int rowColumnToSlot(final int row, final int column) {
        return rowColumnToSlot(row, column, 9);
    }

    /**
     * Convert a zero-based inventory slot to a one-based row and column pair.
     *
     * @param slot    The zero-based slot.
     * @param columns The number of columns per row in the menu.
     * @return The pair of one-based row and one-based column.
     */
    @NotNull
    public static Pair<Integer, Integer> convertSlotToRowColumn(final int slot,
                                                                final int columns) {
        int row = Math.floorDiv(slot, columns);
        int column = slot - row * columns;
        return new Pair<>(row + 1, column + 1);
    }

    /**
     * Convert a one-based row and column to a zero-based inventory slot.
     *
     * @param row     The one-based row.
     * @param column  The one-based column.
     * @param columns The number of columns per row in the menu.
     * @return The zero-based slot.
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
     * @return The menu, or null if the player has no eco {@link Menu} open.
     */
    @Nullable
    public static Menu getOpenMenu(@NotNull final Player player) {
        return Eco.get().getOpenMenu(player);
    }

    private MenuUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
