package com.willfp.eco.core.integrations.shop;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper class for shop integrations.
 */
public interface ShopWrapper extends Integration {
    /**
     * Register eco item provider for shop plugins.
     */
    void registerEcoProvider();
}
