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
 * Wrapper class for shop integrations.
 */
public interface ShopIntegration extends Integration {
    /**
     * Register eco item provider for shop plugins.
     */
    default void registerEcoProvider() {
        // Do nothing unless overridden.
    }

    /**
     * Get the sell event adapter.
     *
     * @return The listener.
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
     * @return If sellable.
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
     * @return The price.
     */
    @NotNull
    default Price getUnitValue(@NotNull final ItemStack itemStack,
                               @NotNull final Player player) {
        return new PriceFree();
    }

}
