package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.EcoPlugin;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle shop integrations.
 */
public final class ShopManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<ShopWrapper> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ShopWrapper integration) {
        REGISTERED.add(integration);
    }

    /**
     * Register the events with eco.
     *
     * @param plugin Instance of eco.
     */
    @ApiStatus.Internal
    public static void registerEvents(@NotNull final EcoPlugin plugin) {
        for (ShopWrapper wrapper : REGISTERED) {
            Listener listener = wrapper.getSellEventAdapter();

            if (listener != null) {
                plugin.getEventManager().registerListener(listener);
            }
        }
    }

    /**
     * Register eco item provider for shop plugins.
     */
    public static void registerEcoProvider() {
        for (ShopWrapper shopWrapper : REGISTERED) {
            shopWrapper.registerEcoProvider();
        }
    }

    private ShopManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
