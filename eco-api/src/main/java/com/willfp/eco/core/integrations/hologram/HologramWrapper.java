package com.willfp.eco.core.integrations.hologram;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Wrapper class for hologram integrations.
 */
public interface HologramWrapper extends Integration {
    /**
     * Create hologram.
     *
     * @param location The location.
     * @param contents The contents for the hologram.
     * @return The hologram.
     */
    Hologram createHologram(@NotNull final Location location,
                            @NotNull final List<String> contents);
}
