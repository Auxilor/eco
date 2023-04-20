package com.willfp.eco.core.integrations.hologram;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Class to handle hologram integrations.
 */
public final class HologramManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<HologramIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final HologramIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Create hologram.
     *
     * @param location The location.
     * @param contents The contents for the hologram.
     * @return The hologram.
     */
    public static Hologram createHologram(@NotNull final Location location,
                                          @NotNull final List<String> contents) {
        return REGISTRY.firstSafely(
                integration -> integration.createHologram(location, contents),
                new DummyHologram()
        );
    }

    private HologramManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
