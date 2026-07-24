package com.willfp.eco.core.integrations.hologram;

import com.willfp.eco.core.Eco;
import java.util.List;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Class to create holograms.
 */
public final class HologramManager {
    /**
     * Create a hologram with default options.
     *
     * @param location The location.
     * @param contents The contents for the hologram.
     * @return The hologram.
     */
    @NotNull
    public static Hologram createHologram(@NotNull final Location location,
                                          @NotNull final List<String> contents) {
        return createHologram(location, HologramOptions.builder().contents(contents).build());
    }

    /**
     * Create a hologram with custom options.
     *
     * @param location The location.
     * @param options  The hologram options.
     * @return The hologram.
     */
    @NotNull
    public static Hologram createHologram(@NotNull final Location location,
                                          @NotNull final HologramOptions options) {
        return Eco.get().createHologram(location, options);
    }

    private HologramManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
