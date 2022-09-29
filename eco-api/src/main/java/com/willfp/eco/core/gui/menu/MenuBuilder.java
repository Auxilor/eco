package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.component.GUIComponent;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Builder to create menus.
 */
public interface MenuBuilder {
    /**
     * Get the amount of rows.
     *
     * @return The amount of rows.
     */
    int getRows();

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
    default MenuBuilder setSlot(final int row,
                                final int column,
                                @NotNull final Slot slot) {
        return this.addComponent(row, column, slot);
    }


    /**
     * Add a component.
     *
     * @param row       The row of the top left corner.
     * @param column    The column of the top left corner.
     * @param component The component.
     * @return The builder.
     */
    MenuBuilder addComponent(int row,
                             int column,
                             @NotNull GUIComponent component);

    /**
     * Run function to modify the builder.
     *
     * @param modifier The modifier.
     * @return The builder.
     */
    MenuBuilder modfiy(@NotNull Consumer<MenuBuilder> modifier);

    /**
     * Set the menu mask.
     *
     * @param mask The mask.
     * @return The builder.
     */
    default MenuBuilder setMask(@NotNull FillerMask mask) {
        return this.addComponent(0, 0, mask);
    }

    /**
     * Set the menu close handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    default MenuBuilder onClose(@NotNull Consumer<InventoryCloseEvent> action) {
        return this.onClose((event, menu) -> action.accept(event));
    }

    /**
     * Set the menu close handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    MenuBuilder onClose(@NotNull CloseHandler action);

    /**
     * Set the menu open handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    MenuBuilder onOpen(@NotNull OpenHandler action);

    /**
     * Set the action to run on render.
     *
     * @param action The action.
     * @return The builder.
     */
    MenuBuilder onRender(@NotNull BiConsumer<Player, Menu> action);

    /**
     * Build the menu.
     *
     * @return The menu.
     */
    Menu build();
}
