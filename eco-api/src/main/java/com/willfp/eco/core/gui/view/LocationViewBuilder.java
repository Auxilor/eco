package com.willfp.eco.core.gui.view;

import org.bukkit.Location;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server-software-agnostic wrapper around {@link LocationInventoryViewBuilder}.
 * <p>
 * Used for views that are backed by a block in the world, such as anvils, furnaces,
 * and enchantment tables.
 *
 * @param <V> The type of view created by this builder.
 * @see ViewBuilders#location(org.bukkit.inventory.MenuType.Typed)
 */
public interface LocationViewBuilder<V extends InventoryView> extends ViewBuilder<V> {
    @Override
    @NotNull
    LocationViewBuilder<V> title(@Nullable String title);

    @Override
    @NotNull
    LocationViewBuilder<V> copy();

    /**
     * Set the location backing the view.
     * <p>
     * The block at the location does not have to match the type of the view; if it doesn't,
     * the view still behaves as its own type. If no location is set, a virtual view is
     * created instead.
     *
     * @param location The location, or null for a virtual view.
     * @return This builder.
     */
    @NotNull
    LocationViewBuilder<V> location(@Nullable Location location);

    /**
     * Set whether the server should check that the player can reach the location.
     * <p>
     * Has no effect on a virtual view (one with no location set).
     *
     * @param checkReachable Whether to check that the view is reachable.
     * @return This builder.
     */
    @NotNull
    LocationViewBuilder<V> checkReachable(boolean checkReachable);
}
