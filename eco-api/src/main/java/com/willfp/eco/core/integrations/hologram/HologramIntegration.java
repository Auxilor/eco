package com.willfp.eco.core.integrations.hologram;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Wrapper class for hologram integrations.
 */
public interface HologramIntegration extends Integration {
    /**
     * Create hologram.
     *
     * @param location The location.
     * @param contents The contents for the hologram.
     * @return The hologram.
     */
    Hologram createHologram(@NotNull Location location,
                            @NotNull List<String> contents);
}
