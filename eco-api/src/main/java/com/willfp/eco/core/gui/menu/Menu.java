package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.page.Page;
import com.willfp.eco.core.gui.slot.Slot;
import com.willfp.eco.util.NamespacedKeyUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GUI version of {@link Inventory}.
 * <p>
 * A menu contains slots and is 1-indexed. (Top row has index 1, bottom row has index 6).
 */
public interface Menu {
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
    default int getColumns() {
        return 9;
    }

    /**
     * Get a static slot at a given row and column.
     * <p>
     * If the slot at the location is reactive, this will return
     * an empty slot.
     *
     * @param row    The row.
     * @param column The column.
     * @return The row.
     */
    Slot getSlot(int row,
                 int column);

    /**
     * Get a slot at a given row and column.
     * <p>
     * Defaults to static slot if no reactive slot exists.
     *
     * @param row    The row.
     * @param column The column.
     * @param player The player
     * @param menu   The menu.
     * @return The slot.
     */
    default Slot getSlot(int row,
                         int column,
                         @NotNull Player player,
                         @NotNull Menu menu) {
        return this.getSlot(row, column);
    }

    /**
     * Get the menu title.
     *
     * @return The title.
     */
    String getTitle();

    /**
     * Open the inventory for the player.
     *
     * @param player The player.
     * @return The inventory.
     */
    Inventory open(@NotNull Player player);

    /**
     * Get captive items.
     *
     * @param player The player.
     * @return The items.
     */
    List<ItemStack> getCaptiveItems(@NotNull Player player);

    /**
     * Get a captive item at a specific position.
     *
     * @param player The player.
     * @param row    The row.
     * @param column The column.
     * @return The captive item.
     */
    @Nullable
    default ItemStack getCaptiveItem(@NotNull final Player player,
                                     final int row,
                                     final int column) {
        return null;
    }

    /**
     * Add state for a player.
     *
     * @param player The player.
     * @param key    The key.
     * @param value  The state.
     */
    void addState(@NotNull Player player,
                  @NotNull String key,
                  @Nullable Object value);

    /**
     * Remove state for a player.
     *
     * @param player The player.
     * @param key    The key.
     */
    void removeState(@NotNull Player player,
                     @NotNull String key);

    /**
     * Clear state for a player.
     *
     * @param player The player.
     */
    void clearState(@NotNull Player player);


    /**
     * Get state for a player.
     *
     * @param player The player.
     * @param key    The key.
     * @param <T>    The type of state.
     * @return The value.
     */
    @Nullable <T> T getState(@NotNull Player player,
                             @NotNull String key);

    /**
     * Get state for a player.
     *
     * @param player The player.
     * @return The state.
     */
    Map<String, Object> getState(@NotNull Player player);

    /**
     * Re-render the menu for a player.
     *
     * @param player The player.
     */
    void refresh(@NotNull Player player);

    /**
     * If the menu allows changing the held item.
     *
     * @return If allowed.
     */
    default boolean allowsChangingHeldItem() {
        return false;
    }

    /**
     * Call a menu event.
     *
     * @param player    The player.
     * @param menuEvent The event.
     */
    default void callEvent(@NotNull final Player player,
                           @NotNull final MenuEvent menuEvent) {
        // Override when needed.
    }

    /**
     * Get the current page a player is on.
     *
     * @param player The player.
     * @return The page.
     */
    default int getPage(@NotNull final Player player) {
        Integer pageState = this.getState(player, Page.PAGE_KEY);
        return Objects.requireNonNullElse(pageState, 1);
    }

    /**
     * Get the max page for a player.
     *
     * @param player The player.
     * @return The page.
     */
    default int getMaxPage(@NotNull final Player player) {
        Integer pageState = this.getState(player, Page.MAX_PAGE_KEY);
        return Objects.requireNonNullElse(pageState, Integer.MAX_VALUE);
    }

    /**
     * Write data.
     *
     * @param player The player.
     * @param key    The key.
     * @param type   The type.
     * @param value  The value.
     * @param <T>    The type.
     * @param <Z>    The type.
     * @deprecated Use addState instead.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    default <T, Z> void writeData(@NotNull final Player player,
                                  @NotNull final NamespacedKey key,
                                  @NotNull final PersistentDataType<T, Z> type,
                                  @NotNull final Z value) {
        this.addState(player, key.toString(), value);
    }

    /**
     * Read data.
     *
     * @param player The player.
     * @param key    The key.
     * @param type   The type.
     * @param <T>    The type.
     * @param <Z>    The type.
     * @return The data.
     * @deprecated Use getState instead.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    default @Nullable <T, Z> T readData(@NotNull final Player player,
                                        @NotNull final NamespacedKey key,
                                        @NotNull final PersistentDataType<T, Z> type) {
        return this.getState(player, key.toString());
    }

    /**
     * Get all data keys for a player.
     *
     * @param player The player.
     * @return The keys.
     * @deprecated Use getState instead.
     */
    @Deprecated(since = "6.35.0", forRemoval = true)
    default Set<NamespacedKey> getKeys(@NotNull final Player player) {
        return this.getState(player).keySet().stream()
                .map(NamespacedKeyUtils::fromStringOrNull)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Create a builder with a given amount of rows.
     *
     * @param rows The rows.
     * @return The builder.
     */
    static MenuBuilder builder(final int rows) {
        return Eco.get().createMenuBuilder(
                rows,
                MenuType.NORMAL
        );
    }

    /**
     * Create a builder with a given type.
     *
     * @param type The menu type.
     * @return The builder.
     */
    static MenuBuilder builder(@NotNull final MenuType type) {
        return Eco.get().createMenuBuilder(type.getDefaultRows(), type);
    }
}
