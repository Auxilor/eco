package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * GUI version of {@link Inventory}.
 * <p>
 * A menu contains slots and is 1-indexed. (Top row has index 1, bottom row has index 6).
 */
public interface Menu {
    /**
     * Get the amount of rows.
     *
     * @return The amount of rows.
     */
    int getRows();

    /**
     * Get slot at given row and column.
     *
     * @param row    The row.
     * @param column The column.
     * @return The row.
     */
    Slot getSlot(int row,
                 int column);

    /**
     * Get the menu title.
     *
     * @return The title.
     */
    String getTitle();

    /**
     * Open the inventory for the player.
     *
     * @param player The player.
     * @return The inventory.
     */
    Inventory open(@NotNull Player player);

    /**
     * Get captive items.
     *
     * @param player The player.
     * @return The items.
     */
    List<ItemStack> getCaptiveItems(@NotNull Player player);

    /**
     * Create a builder with a given amount of rows.
     *
     * @param rows The rows.
     * @return The builder.
     */
    static MenuBuilder builder(final int rows) {
        return Eco.getHandler().getGUIFactory().createMenuBuilder(rows);
    }
}
