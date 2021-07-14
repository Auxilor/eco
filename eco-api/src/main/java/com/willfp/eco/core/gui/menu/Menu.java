package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
     * Create a builder with a given amount of rows.
     *
     * @param rows The rows.
     * @return The builder.
     */
    static Builder builder(final int rows) {
        return Eco.getHandler().getGUIFactory().createMenuBuilder(rows);
    }

    interface Builder {
        Builder setTitle(@NotNull String title);

        Builder setSlot(int row,
                        int column,
                        @NotNull Slot slot);

        Builder setMask(@NotNull FillerMask mask);

        Builder onClose(@NotNull Consumer<InventoryCloseEvent> action);

        Menu build();
    }
}
