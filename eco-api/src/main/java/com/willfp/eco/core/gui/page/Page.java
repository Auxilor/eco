package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.component.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A page is a component representing another menu.
 * This allows full component support in pagination.
 */
public final class Page implements GUIComponent {
    /**
     * The Menu state key for the current page.
     */
    public static final String PAGE_KEY = "page";

    /**
     * The Menu state key for the amount of pages.
     */
    public static final String MAX_PAGE_KEY = "max_page";

    /**
     * The page number.
     */
    private final int pageNumber;

    /**
     * The base menu.
     */
    private final Menu page;

    /**
     * The delegate menu.
     */
    private Menu delegate = null;

    /**
     * Create a new page.
     *
     * @param pageNumber The page number.
     * @param page       The base menu.
     */
    public Page(final int pageNumber,
                @NotNull final Menu page) {
        this.pageNumber = pageNumber;
        this.page = page;
    }

    /**
     * Create a new page.
     *
     * @param pageNumber The page number.
     * @param page       The base menu.
     */
    public Page(final int pageNumber,
                @NotNull final Consumer<PageBuilder> page) {
        this.pageNumber = pageNumber;
        MenuBuilder builder = Menu.builder(6);
        page.accept(builder);
        this.page = builder.build();
    }

    /**
     * Get the current page number.
     *
     * @return The page number.
     */
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public @Nullable Slot getSlotAt(final int row,
                                    final int column,
                                    @NotNull final Player player,
                                    @NotNull final Menu menu) {
        if (getPage(player, menu) != pageNumber) {
            return null;
        }

        if (delegate == null) {
            delegate = Eco.getHandler().getGUIFactory().blendMenuState(page, menu);
        }

        return page.getSlot(row, column, player, delegate);
    }

    @Override
    public int getRows() {
        return page.getRows();
    }

    @Override
    public int getColumns() {
        return 9;
    }

    /**
     * Get the page.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The page.
     */
    public static int getPage(@NotNull final Player player,
                              @NotNull final Menu menu) {
        Integer pageState = menu.getState(player, Page.PAGE_KEY);
        return Objects.requireNonNullElse(pageState, 1);
    }

    /**
     * Get the page.
     *
     * @param player The player.
     * @param menu   The menu.
     * @return The page.
     */
    public static int getMaxPage(@NotNull final Player player,
                                 @NotNull final Menu menu) {
        Integer pageState = menu.getState(player, Page.MAX_PAGE_KEY);
        return Objects.requireNonNullElse(pageState, Integer.MAX_VALUE);
    }
}
