package com.willfp.eco.core.integrations.shop;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle shop integrations.
 */
@UtilityClass
public class ShopManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<ShopWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final ShopWrapper integration) {
        registered.add(integration);
    }

    /**
     * Register eco item provider for shop plugins.
     */
    public void registerEcoProvider() {
        for (ShopWrapper shopWrapper : registered) {
            shopWrapper.registerEcoProvider();
        }
    }
}
