package com.willfp.eco.core.gui.menu;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.gui.slot.Slot;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

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
     * Get slot at given row and column.
     *
     * @param row    The row.
     * @param column The column.
     * @return The row.
     */
    Slot getSlot(int row,
                 int column);

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
     * Write data.
     *
     * @param player The player.
     * @param key    The key.
     * @param type   The type.
     * @param value  The value.
     * @param <T>    The type.
     * @param <Z>    The type.
     */
    <T, Z> void writeData(@NotNull Player player,
                          @NotNull NamespacedKey key,
                          @NotNull PersistentDataType<T, Z> type,
                          @NotNull Z value);

    /**
     * Read data.
     *
     * @param player The player.
     * @param key    The key.
     * @param type   The type.
     * @param <T>    The type.
     * @param <Z>    The type.
     * @return The data.
     */
    @Nullable <T, Z> T readData(@NotNull Player player,
                                @NotNull NamespacedKey key,
                                @NotNull PersistentDataType<T, Z> type);

    /**
     * Get all data keys for a player.
     *
     * @param player The player.
     * @return The keys.
     */
    Set<NamespacedKey> getKeys(@NotNull Player player);

    /**
     * Create a builder with a given amount of rows.
     *
     * @param rows The rows.
     * @return The builder.
     */
    static MenuBuilder builder(final int rows) {
        return Eco.getHandler().getGUIFactory().createMenuBuilder(rows);
    }
}
