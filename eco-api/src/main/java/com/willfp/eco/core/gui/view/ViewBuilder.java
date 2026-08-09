package com.willfp.eco.core.gui.view;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server-software-agnostic wrapper around {@link InventoryViewBuilder}.
 * <p>
 * Bukkit's own builders take the view title in a type that differs between server
 * implementations - Spigot takes a legacy {@link String}, Paper takes an Adventure
 * {@code Component} - so code compiled against one fails at runtime on the other.
 * This builder takes a legacy-formatted string on every platform and converts it
 * to whatever the running server expects.
 *
 * @param <V> The type of view created by this builder.
 * @see ViewBuilders
 */
public interface ViewBuilder<V extends InventoryView> {
    /**
     * Set the title of the view.
     *
     * @param title The title, legacy-formatted (e.g. {@code "&8Trades"}), or null for the default title.
     * @return This builder.
     */
    @NotNull
    ViewBuilder<V> title(@Nullable String title);

    /**
     * Build the view.
     * <p>
     * Building does not show the view to the player; pass it to
     * {@link HumanEntity#openInventory(InventoryView)} to do that, or use {@link #open(HumanEntity)}.
     *
     * @param player The player to build the view for.
     * @return The view.
     */
    @NotNull
    V build(@NotNull HumanEntity player);

    /**
     * Build the view and open it for the player.
     *
     * @param player The player to open the view for.
     * @return The view that was opened.
     */
    @NotNull
    default V open(@NotNull HumanEntity player) {
        V view = this.build(player);
        player.openInventory(view);
        return view;
    }

    /**
     * Make a copy of this builder.
     *
     * @return The copy.
     */
    @NotNull
    ViewBuilder<V> copy();
}
