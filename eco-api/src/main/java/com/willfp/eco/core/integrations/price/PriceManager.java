package com.willfp.eco.core.integrations.price;

import com.willfp.eco.core.price.PriceFactory;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to handle afk integrations.
 */
public final class PriceManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<PriceFactory> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final PriceFactory integration) {
        REGISTERED.removeIf(it -> new HashSet<>(integration.getNames()).containsAll(it.getNames()));
        REGISTERED.add(integration);
    }

    private PriceManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
