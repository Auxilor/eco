package com.willfp.eco.core.integrations.custombiomes;

import com.willfp.eco.core.integrations.Integration;
import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Wrapper class for custom biome integrations.
 */
public interface CustomBiomesIntegration extends Integration {
    /**
     * Get a biome at given location. (Supports vanilla biomes as well)
     *
     * @param location The location to get the biome at.
     * @return         The found biome, null otherwise
     */
    @Nullable
    CustomBiome getBiome(Location location);
}
