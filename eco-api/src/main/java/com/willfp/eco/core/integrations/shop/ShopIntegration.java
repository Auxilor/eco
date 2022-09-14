package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.Integration;
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
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @return The price.
     */
    default double getPrice(@NotNull final ItemStack itemStack) {
        // Do nothing unless overridden.
        return 0.0;
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The price.
     */
    default double getPrice(@NotNull final ItemStack itemStack,
                            @NotNull final Player player) {
        return getPrice(itemStack);
    }
}
