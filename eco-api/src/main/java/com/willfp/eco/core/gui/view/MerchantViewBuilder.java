package com.willfp.eco.core.gui.view;

import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.view.builder.MerchantInventoryViewBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Server-software-agnostic wrapper around {@link MerchantInventoryViewBuilder}.
 * <p>
 * Opens a trading screen for a {@link Merchant}, which does not have to be a villager -
 * a virtual merchant from {@link org.bukkit.Bukkit#createMerchant()} works too, so trades
 * can be offered without any entity existing.
 *
 * @param <V> The type of view created by this builder.
 * @see ViewBuilders#merchant()
 */
public interface MerchantViewBuilder<V extends InventoryView> extends ViewBuilder<V> {
    @Override
    @NotNull
    MerchantViewBuilder<V> title(@Nullable String title);

    @Override
    @NotNull
    MerchantViewBuilder<V> copy();

    /**
     * Set the merchant the view trades with.
     *
     * @param merchant The merchant.
     * @return This builder.
     */
    @NotNull
    MerchantViewBuilder<V> merchant(@NotNull Merchant merchant);

    /**
     * Set whether the server should check that the player can reach the merchant.
     * <p>
     * Has no effect on a virtual merchant.
     *
     * @param checkReachable Whether to check that the view is reachable.
     * @return This builder.
     */
    @NotNull
    MerchantViewBuilder<V> checkReachable(boolean checkReachable);
}
