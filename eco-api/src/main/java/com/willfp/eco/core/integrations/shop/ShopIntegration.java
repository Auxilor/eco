package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.Integration;
import com.willfp.eco.core.price.Price;
import com.willfp.eco.core.price.impl.PriceFree;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper interface for shop integrations.
 * <p>
 * Implemented for shop plugins such as ShopGUIPlus, EconomyShopGUI, ExcellentShop, zShop, and
 * DeluxeSellwands, so that eco can query item sell values and hook their sell events into
 * {@link ShopSellEvent}.
 *
 * @see ShopManager
 */
public interface ShopIntegration extends Integration {
    /**
     * Register eco's item provider with the shop plugin, so that eco items can be used in its
     * configs.
     */
    default void registerEcoProvider() {
        // Do nothing unless overridden.
    }

    /**
     * Get the listener that adapts the shop plugin's own sell event into a {@link ShopSellEvent}.
     *
     * @return The listener, or null if the integration does not provide one.
     */
    @Nullable
    default Listener getSellEventAdapter() {
        // Do nothing unless overridden.
        return null;
    }

    /**
     * Get if an item is sellable for a player.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return If sellable. False unless overridden.
     */
    default boolean isSellable(@NotNull final ItemStack itemStack,
                               @NotNull final Player player) {
        return false;
    }

    /**
     * Get the value of one of an item for a player.
     * <p>
     * For example, if you pass in a stack, it will only return the value of <b>one</b> item, not the full stack.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The price. A {@link PriceFree} unless overridden.
     */
    @NotNull
    default Price getUnitValue(@NotNull final ItemStack itemStack,
                               @NotNull final Player player) {
        return new PriceFree();
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @return The price. Always 0 unless overridden, since prices depend on the player.
     * @deprecated Use {@link #getUnitValue(ItemStack, Player)} instead.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    default double getPrice(@NotNull final ItemStack itemStack) {
        // Do nothing unless overridden.
        return 0.0;
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The unit price, resolved for the player.
     * @deprecated Use {@link #getUnitValue(ItemStack, Player)} instead.
     */
    @Deprecated(since = "6.47.0", forRemoval = true)
    default double getPrice(@NotNull final ItemStack itemStack,
                            @NotNull final Player player) {
        return getUnitValue(itemStack, player).getValue(player);
    }
}
