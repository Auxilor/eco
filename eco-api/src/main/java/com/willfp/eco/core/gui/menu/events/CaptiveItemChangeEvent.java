package com.willfp.eco.core.gui.menu.events;

import com.willfp.eco.core.gui.menu.MenuEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a captive item change.
 *
 * @param row    The row.
 * @param column The column.
 * @param before The previous item in the slot.
 * @param after  The new item in the slot.
 */
public record CaptiveItemChangeEvent(
        int row,
        int column,
        @Nullable ItemStack before,
        @Nullable ItemStack after
) implements MenuEvent {

}
