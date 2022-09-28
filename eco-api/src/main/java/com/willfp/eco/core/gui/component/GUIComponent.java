package com.willfp.eco.core.gui.component;

import com.willfp.eco.core.gui.slot.Slot;
import org.jetbrains.annotations.Nullable;

/**
 * A GUI Component is a 2-dimensional set of slots that can be
 * placed in a menu.
 */
public interface GUIComponent {
    /**
     * Get the amount of rows in the component.
     *
     * @return The rows.
     */
    int getRows();

    /**
     * Get the amount of columns in the component.
     *
     * @return The columns.
     */
    int getColumns();

    /**
     * Get the slot at a certain position in the component.
     *
     * @param row    The row (1-indexed).
     * @param column The column (1-indexed).
     * @return The slot, or null if no slot at the location.
     */
    @Nullable
    Slot getSlotAt(final int row,
                   final int column);
}
