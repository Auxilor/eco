package com.willfp.eco.util;

import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.tuples.Pair;
import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Utilities / API methods for menus.
 */
public final class MenuUtils {
    /**
     * The menu supplier.
     */
    private static Function<Player, Menu> menuGetter = null;

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

    /**
     * Convert row and column to 0-53 slot.
     *
     * @param row    The row.
     * @param column The column.
     * @return The slot.
     */
    public static int rowColumnToSlot(final int row, final int column) {
        return (column - 1) + ((row - 1) * 9);
    }

    /**
     * Get a player's open menu.
     *
     * @param player The player.
     * @return The menu, or null if none open.
     */
    @Nullable
    public static Menu getOpenMenu(@NotNull final Player player) {
        return menuGetter.apply(player);
    }

    /**
     * Initialize the tps supplier function.
     *
     * @param function The function.
     */
    @ApiStatus.Internal
    public static void initialize(@NotNull final Function<Player, Menu> function) {
        Validate.isTrue(menuGetter == null, "Already initialized!");

        menuGetter = function;
    }

    private MenuUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
