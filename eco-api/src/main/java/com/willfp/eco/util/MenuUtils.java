package com.willfp.eco.util;

import com.willfp.eco.core.tuples.Pair;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for menus.
 */
@UtilityClass
public class MenuUtils {
    /**
     * Convert 0-53 slot to row and column pair.
     *
     * @param slot The slot.
     * @return The pair of row and columns.
     */
    @NotNull
    public Pair<Integer, Integer> convertSlotToRowColumn(final int slot) {
        int row = Math.floorDiv(slot, 9);
        int column = slot - row * 9;
        return new Pair<>(row + 1, column + 1);
    }
}
