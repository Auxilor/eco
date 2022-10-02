package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.page.Page;
import com.willfp.eco.core.gui.page.PageBuilder;
import com.willfp.eco.core.gui.slot.FillerMask;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Builder to create menus.
 */
public interface MenuBuilder extends PageBuilder {
    /**
     * Set the menu title.
     *
     * @param title The title.
     * @return The builder.
     */
    MenuBuilder setTitle(@NotNull String title);

    /**
     * Get the menu title.
     *
     * @return The builder.
     */
    default String getTitle() {
        return "";
    }

    /**
     * Set a slot.
     *
     * @param row    The row.
     * @param column The column.
     * @param slot   The slot.
     * @return The builder.
     */
    @Override
    default MenuBuilder setSlot(final int row,
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
    @Override
    MenuBuilder addComponent(@NotNull MenuLayer layer,
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
    @Override
    default MenuBuilder addComponent(final int row,
                                     final int column,
                                     @NotNull final GUIComponent component) {
        return this.addComponent(MenuLayer.MIDDLE, row, column, component);
    }

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
    @Override
    default MenuBuilder setMask(@NotNull final FillerMask mask) {
        return this.addComponent(MenuLayer.BACKGROUND, 1, 1, mask);
    }

    /**
     * Add a page.
     *
     * @param page The page.
     * @return The builder.
     */
    default MenuBuilder addPage(@NotNull final Page page) {
        return this.addComponent(MenuLayer.TOP, 1, 1, page);
    }

    /**
     * Add a page.
     *
     * @param pageNumber The page number.
     * @param builder    The page builder.
     * @return The builder.
     */
    default MenuBuilder addPage(final int pageNumber,
                                @NotNull final PageBuilder builder) {
        return this.addPage(new Page(pageNumber, ((MenuBuilder) builder).build()));
    }

    /**
     * Set the max pages.
     *
     * @param pages The max pages.
     * @return The builder.
     */
    default MenuBuilder maxPages(final int pages) {
        return this.maxPages(player -> pages);
    }

    /**
     * Set the max pages dynamically for a player.
     *
     * @param pages The max pages.
     * @return The builder.
     */
    default MenuBuilder maxPages(@NotNull final Function<Player, Integer> pages) {
        return onOpen((player, menu) -> menu.addState(player, Page.MAX_PAGE_KEY, pages.apply(player)));
    }

    /**
     * Add a menu close handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    default MenuBuilder onClose(@NotNull final Consumer<InventoryCloseEvent> action) {
        return this.onClose((event, menu) -> action.accept(event));
    }

    /**
     * Add a menu close handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    MenuBuilder onClose(@NotNull CloseHandler action);

    /**
     * Add a menu open handler.
     *
     * @param action The handler.
     * @return The builder.
     */
    MenuBuilder onOpen(@NotNull OpenHandler action);

    /**
     * Add an action to run on render.
     *
     * @param action The action.
     * @return The builder.
     */
    MenuBuilder onRender(@NotNull BiConsumer<Player, Menu> action);

    /**
     * Add an action to run on an event.
     *
     * @param action The action.
     * @return The builder.
     */
    default MenuBuilder onEvent(@NotNull final MenuEventHandler<?> action) {
        return this;
    }

    /**
     * Allow the player to change their held item.
     *
     * @return The builder.
     */
    default MenuBuilder allowChangingHeldItem() {
        return this;
    }

    /**
     * Build the menu.
     *
     * @return The menu.
     */
    Menu build();
}
