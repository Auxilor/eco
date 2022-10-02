package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A slot loaded in from config.
 */
public final class PageChanger implements GUIComponent {
    /**
     * The slot to be shown.
     */
    private final Slot slot;

    /**
     * The direction to turn the page.
     */
    private final Direction direction;

    /**
     * Create a new page change slot.
     *
     * @param itemStack The ItemStack.
     * @param direction The direction.
     */
    public PageChanger(@NotNull final ItemStack itemStack,
                       @NotNull final Direction direction) {
        this.direction = direction;

        slot = Slot.builder(itemStack)
                .onLeftClick((event, slot, menu) -> {
                    Player player = (Player) event.getWhoClicked();
                    int page = menu.getPage(player);
                    int newPage = Math.max(
                            1,
                            Math.min(
                                    page + direction.getChange(),
                                    menu.getMaxPage(player)
                            )
                    );

                    if (newPage == page) {
                        return;
                    }

                    menu.addState(player, Page.PAGE_KEY, newPage);
                    menu.callEvent(player, new PageChangeEvent(
                            newPage,
                            page
                    ));
                })
                .build();
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public int getColumns() {
        return 1;
    }

    @Override
    public @Nullable Slot getSlotAt(final int row,
                                    final int column,
                                    @NotNull final Player player,
                                    @NotNull final Menu menu) {
        int page = menu.getPage(player);
        int maxPage = menu.getMaxPage(player);

        if (page <= 1 && this.direction == Direction.BACKWARDS) {
            return null;
        }

        if (page >= maxPage - 1 && this.direction == Direction.FORWARDS) {
            return null;
        }

        return slot;
    }

    /**
     * The direction to change the page.
     */
    public enum Direction {
        /**
         * Increment the page by 1.
         */
        FORWARDS(1),

        /**
         * Decrement the page by 1.
         */
        BACKWARDS(-1);

        /**
         * The amount of pages to change by.
         */
        private final int change;

        /**
         * Create a new direction.
         *
         * @param change The amount of pages to change by.
         */
        Direction(final int change) {
            this.change = change;
        }

        /**
         * Get the amount of pages to change by.
         *
         * @return The change.
         */
        public int getChange() {
            return change;
        }
    }
}
