package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Builder to create menus.
 */
public interface MenuBuilder {
    /**
     * Set the menu title.
     *
     * @param title The title.
     * @return The builder.
     */
    MenuBuilder setTitle(@NotNull String title);

    /**
     * Set a slot.
     *
     * @param row    The row.
     * @param column The column.
     * @param slot   The slot.
     * @return The builder.
     */
    MenuBuilder setSlot(int row,
                        int column,
                        @NotNull Slot slot);

    /**
     * Set the menu mask.
     *
     * @param mask The mask.
     * @return The builder.
     */
    MenuBuilder setMask(@NotNull FillerMask mask);

    /**
     * Set the menu close handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    MenuBuilder onClose(@NotNull Consumer<InventoryCloseEvent> action);

    /**
     * Build the menu.
     *
     * @return The menu.
     */
    Menu build();
}
