package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.menu.MenuBuilder;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
     * The rows for the page to have.
     */
    private int rows = 6;

    /**
     * The columns for the page to have.
     */
    private int columns = 9;

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
        if (menu.getPage(player) != pageNumber) {
            return null;
        }

        if (delegate == null) {
            delegate = Eco.get().blendMenuState(page, menu);
        }

        return page.getSlot(row, column, player, delegate);
    }

    @Override
    public void init(final int maxRows,
                     final int maxColumns) {
        this.rows = maxRows;
        this.columns = maxColumns;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public int getColumns() {
        return columns;
    }

    /**
     * Create a new page builder.
     *
     * @param context The context to create the page for.
     * @return The page builder.
     */
    public static PageBuilder builder(@NotNull final MenuBuilder context) {
        return Menu.builder(context.getRows());
    }
}
