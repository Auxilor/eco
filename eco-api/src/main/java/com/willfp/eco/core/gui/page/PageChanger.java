package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.gui.GUIComponent;
import com.willfp.eco.core.gui.menu.Menu;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.core.sound.PlayableSound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A page change button.
 * <p>
 * Shows an active item while a turn is possible, and an optional inactive item
 * on the first or last page. If no inactive item is set the button is hidden,
 * which is the legacy behaviour. The placeholders {@code %page%} and
 * {@code %max_page%} in the item display name and lore are substituted with the
 * current page and max page on every render. An optional sound is played when
 * the page turns.
 */
public final class PageChanger implements GUIComponent {
    /**
     * The direction to turn the page.
     */
    private final Direction direction;

    /**
     * The item shown while a turn is possible.
     */
    private final ItemStack activeItem;

    /**
     * The item shown on the first or last page, or null to hide the button.
     */
    private final ItemStack inactiveItem;

    /**
     * The sound played when the page turns, or null for silent.
     */
    private final PlayableSound sound;

    /**
     * Create a new page change button.
     *
     * @param itemStack The ItemStack.
     * @param direction The direction.
     */
    public PageChanger(@NotNull final ItemStack itemStack,
                       @NotNull final Direction direction) {
        this(direction, itemStack, null, null);
    }

    private PageChanger(@NotNull final Direction direction,
                        @NotNull final ItemStack activeItem,
                        @Nullable final ItemStack inactiveItem,
                        @Nullable final PlayableSound sound) {
        this.direction = direction;
        this.activeItem = activeItem;
        this.inactiveItem = inactiveItem;
        this.sound = sound;
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

        boolean inactive = (page <= 1 && this.direction == Direction.BACKWARDS)
                || (page >= maxPage && this.direction == Direction.FORWARDS);

        if (inactive) {
            if (this.inactiveItem == null) {
                return null;
            }

            return Slot.builder(withPagePlaceholders(this.inactiveItem, page, maxPage)).build();
        }

        return Slot.builder(withPagePlaceholders(this.activeItem, page, maxPage))
                .onLeftClick((event, slot, clickedMenu) -> {
                    Player clicker = (Player) event.getWhoClicked();
                    int current = clickedMenu.getPage(clicker);
                    int newPage = Math.max(
                            1,
                            Math.min(
                                    current + direction.getChange(),
                                    clickedMenu.getMaxPage(clicker)
                            )
                    );

                    if (newPage == current) {
                        return;
                    }

                    clickedMenu.setState(clicker, Page.PAGE_KEY, newPage);
                    clickedMenu.callEvent(clicker, new PageChangeEvent(
                            newPage,
                            current
                    ));

                    if (this.sound != null) {
                        this.sound.playTo(clicker);
                    }
                })
                .build();
    }

    private static ItemStack withPagePlaceholders(@NotNull final ItemStack item,
                                                  final int page,
                                                  final int maxPage) {
        ItemMeta meta = item.getItemMeta();

        if (meta == null) {
            return item;
        }

        String name = meta.hasDisplayName() ? meta.getDisplayName() : null;
        List<String> lore = meta.getLore();

        boolean nameHasTokens = name != null && hasTokens(name);
        boolean loreHasTokens = false;

        if (lore != null) {
            for (String line : lore) {
                if (hasTokens(line)) {
                    loreHasTokens = true;
                    break;
                }
            }
        }

        if (!nameHasTokens && !loreHasTokens) {
            return item;
        }

        ItemStack clone = item.clone();
        ItemMeta cloneMeta = clone.getItemMeta();

        if (nameHasTokens) {
            cloneMeta.setDisplayName(substitute(name, page, maxPage));
        }

        if (loreHasTokens) {
            List<String> newLore = new ArrayList<>(lore.size());
            for (String line : lore) {
                newLore.add(substitute(line, page, maxPage));
            }
            cloneMeta.setLore(newLore);
        }

        clone.setItemMeta(cloneMeta);
        return clone;
    }

    private static boolean hasTokens(@NotNull final String string) {
        return string.contains("%page%") || string.contains("%max_page%");
    }

    private static String substitute(@NotNull final String string,
                                     final int page,
                                     final int maxPage) {
        return string.replace("%page%", String.valueOf(page))
                .replace("%max_page%", String.valueOf(maxPage));
    }

    /**
     * Create a new page changer builder.
     *
     * @param direction The direction.
     * @return The builder.
     */
    public static Builder builder(@NotNull final Direction direction) {
        return new Builder(direction);
    }

    /**
     * Builder for {@link PageChanger}.
     */
    public static final class Builder {
        /**
         * The direction.
         */
        private final Direction direction;

        /**
         * The active item.
         */
        private ItemStack activeItem = null;

        /**
         * The inactive item.
         */
        private ItemStack inactiveItem = null;

        /**
         * The sound.
         */
        private PlayableSound sound = null;

        private Builder(@NotNull final Direction direction) {
            this.direction = direction;
        }

        /**
         * Set the item shown while a turn is possible.
         *
         * @param item The item.
         * @return The builder.
         */
        public Builder active(@NotNull final ItemStack item) {
            this.activeItem = item;
            return this;
        }

        /**
         * Set the item shown on the first or last page. If null the button is
         * hidden on that page, which is the default.
         *
         * @param item The item, or null to hide.
         * @return The builder.
         */
        public Builder inactive(@Nullable final ItemStack item) {
            this.inactiveItem = item;
            return this;
        }

        /**
         * Set the sound played when the page turns.
         *
         * @param sound The sound, or null for silent.
         * @return The builder.
         */
        public Builder sound(@Nullable final PlayableSound sound) {
            this.sound = sound;
            return this;
        }

        /**
         * Build the page changer.
         *
         * @return The page changer.
         */
        public PageChanger build() {
            if (this.activeItem == null) {
                throw new IllegalStateException("active item must be set");
            }

            return new PageChanger(this.direction, this.activeItem, this.inactiveItem, this.sound);
        }
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
