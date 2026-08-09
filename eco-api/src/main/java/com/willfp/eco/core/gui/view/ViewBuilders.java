package com.willfp.eco.core.gui.view;

import com.willfp.eco.core.Eco;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.view.MerchantView;
import org.bukkit.inventory.view.builder.InventoryViewBuilder;
import org.bukkit.inventory.view.builder.LocationInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;

/**
 * Create {@link ViewBuilder}s: server-software-agnostic versions of Bukkit's
 * {@link InventoryViewBuilder}s.
 * <p>
 * Use these instead of {@link MenuType.Typed#builder()} directly - the Bukkit builders
 * take their title in a different type on Spigot than on Paper, so calling them from a
 * plugin compiled against one fails at runtime on the other.
 * <p>
 * These builders are for vanilla-backed views (anvils, merchants, and the like).
 * For a regular chest-style GUI, use {@link com.willfp.eco.core.gui.menu.Menu}.
 */
public final class ViewBuilders {
    /**
     * Create a builder for any menu type.
     *
     * @param type The menu type.
     * @param <V>  The type of view created by the builder.
     * @return The builder.
     */
    @NotNull
    public static <V extends InventoryView> ViewBuilder<V> of(
            @NotNull final MenuType.Typed<V, ? extends InventoryViewBuilder<V>> type
    ) {
        return Eco.get().createViewBuilder(type);
    }

    /**
     * Create a builder for a menu type backed by a block in the world, for example
     * {@link MenuType#ANVIL} or {@link MenuType#ENCHANTMENT}.
     *
     * @param type The menu type.
     * @param <V>  The type of view created by the builder.
     * @return The builder.
     */
    @NotNull
    public static <V extends InventoryView> LocationViewBuilder<V> location(
            @NotNull final MenuType.Typed<V, LocationInventoryViewBuilder<V>> type
    ) {
        return Eco.get().createLocationViewBuilder(type);
    }

    /**
     * Create a builder for {@link MenuType#MERCHANT}, the villager trading screen.
     *
     * @return The builder.
     */
    @NotNull
    public static MerchantViewBuilder<MerchantView> merchant() {
        return Eco.get().createMerchantViewBuilder();
    }

    private ViewBuilders() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
