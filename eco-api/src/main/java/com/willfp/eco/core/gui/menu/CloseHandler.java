package com.willfp.eco.core.gui.menu;

import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to run on menu close.
 */
@FunctionalInterface
public interface CloseHandler {
    /**
     * Performs this operation on the given arguments.
     *
     * @param event The close event.
     * @param menu  The menu.
     */
    void handle(@NotNull InventoryCloseEvent event,
                @NotNull Menu menu);
}
