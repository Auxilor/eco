package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuLayer;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * Builder to create pages.
 */
public interface PageBuilder {
    /**
     * Get the amount of rows.
     *
     * @return The amount of rows.
     */
    int getRows();

    /**
     * Get the amount of columns.
     *
     * @return The amount of columns.
     */
    int getColumns();

    /**
     * Set a slot.
     *
     * @param row    The row.
     * @param column The column.
     * @param slot   The slot.
     * @return The builder.
     */
    default PageBuilder setSlot(final int row,
                                final int column,
                                @NotNull final Slot slot) {
        return this.addComponent(row, column, slot);
    }


    /**
     * Add a component.
     *
     * @param layer     The layer.
     * @param row       The row of the top left corner.
     * @param column    The column of the top left corner.
     * @param component The component.
     * @return The builder.
     */
    PageBuilder addComponent(@NotNull MenuLayer layer,
                             int row,
                             int column,
                             @NotNull GUIComponent component);


    /**
     * Add a component.
     *
     * @param row       The row of the top left corner.
     * @param column    The column of the top left corner.
     * @param component The component.
     * @return The builder.
     */
    default PageBuilder addComponent(final int row,
                                     final int column,
                                     @NotNull final GUIComponent component) {
        return this.addComponent(MenuLayer.MIDDLE, row, column, component);
    }

    /**
     * Set the menu mask.
     *
     * @param mask The mask.
     * @return The builder.
     */
    default PageBuilder setMask(@NotNull final FillerMask mask) {
        return this.addComponent(MenuLayer.BACKGROUND, 1, 1, mask);
    }

    /**
     * Set the action to run on render.
     *
     * @param action The action.
     * @return The builder.
     */
    PageBuilder onRender(@NotNull BiConsumer<Player, Menu> action);
}
