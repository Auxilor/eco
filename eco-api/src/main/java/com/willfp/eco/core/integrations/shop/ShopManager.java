package com.willfp.eco.core.integrations.shop;

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
    private static final Set<ShopWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final ShopWrapper integration) {
        registered.add(integration);
    }

    /**
     * Register eco item provider for shop plugins.
     */
    public static void registerEcoProvider() {
        for (ShopWrapper shopWrapper : registered) {
            shopWrapper.registerEcoProvider();
        }
    }

    private ShopManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
