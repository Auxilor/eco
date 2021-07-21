package com.willfp.eco.core.integrations.customitems;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle custom item integrations.
 */
@UtilityClass
public class CustomItemsManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<CustomItemsWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final CustomItemsWrapper integration) {
        registered.add(integration);
    }

    /**
     * Register all the custom items for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.items.Items
     */
    public void registerAllItems() {
        for (CustomItemsWrapper customItemsWrapper : registered) {
            customItemsWrapper.registerAllItems();
        }
    }
}
