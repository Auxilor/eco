package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle shop integrations.
 */
public final class ShopManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<ShopIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ShopIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
    }

    /**
     * Register eco item provider for shop plugins.
     */
    public static void registerEcoProvider() {
        for (ShopIntegration shopIntegration : REGISTERED) {
            shopIntegration.registerEcoProvider();
        }
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @return The price.
     */
    public static double getItemPrice(@Nullable final ItemStack itemStack) {
        return getItemPrice(itemStack, null);
    }

    /**
     * Get the price of an item.
     *
     * @param itemStack The item.
     * @param player    The player.
     * @return The price.
     */
    public static double getItemPrice(@Nullable final ItemStack itemStack,
                                      @Nullable final Player player) {
        if (itemStack == null) {
            return 0.0;
        }

        for (ShopIntegration shopIntegration : REGISTERED) {
            if (player == null) {
                return shopIntegration.getPrice(itemStack);
            } else {
                return shopIntegration.getPrice(itemStack, player);
            }
        }

        return 0.0;
    }

    /**
     * Get all registered integrations.
     *
     * @return The integrations.
     */
    public static Set<ShopIntegration> getRegisteredIntegrations() {
        return new HashSet<>(REGISTERED);
    }

    private ShopManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
