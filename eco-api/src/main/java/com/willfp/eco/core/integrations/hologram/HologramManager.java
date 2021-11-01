package com.willfp.eco.core.integrations.hologram;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to handle hologram integrations.
 */
@UtilityClass
public class HologramManager {
    /**
     * A set of all registered integrations.
     */
    private final Set<HologramWrapper> registered = new HashSet<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public void register(@NotNull final HologramWrapper integration) {
        registered.add(integration);
    }

    /**
     * Create hologram.
     *
     * @param location The location.
     * @param contents The contents for the hologram.
     * @return The hologram.
     */
    public Hologram createHologram(@NotNull final Location location,
                                   @NotNull final List<String> contents) {
        for (HologramWrapper wrapper : registered) {
            return wrapper.createHologram(location, contents);
        }

        return new DummyHologram();
    }
}
