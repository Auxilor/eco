package com.willfp.eco.core.integrations.hologram;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to handle hologram integrations.
 */
public final class HologramManager {
    /**
     * A set of all registered integrations.
     */
    private static final Set<HologramIntegration> REGISTERED = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final HologramIntegration integration) {
        REGISTERED.removeIf(it -> it.getPluginName().equalsIgnoreCase(integration.getPluginName()));
        REGISTERED.add(integration);
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
        for (HologramIntegration integration : REGISTERED) {
            return integration.createHologram(location, contents);
        }

        return new DummyHologram();
    }

    private HologramManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
