package com.willfp.eco.util;

import com.willfp.eco.core.tuples.Pair;
import org.jetbrains.annotations.NotNull;

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
        int row = Math.floorDiv(slot, 9);
        int column = slot - row * 9;
        return new Pair<>(row + 1, column + 1);
    }

    private MenuUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
