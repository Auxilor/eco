package com.willfp.eco.core.integrations.custombiomes;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CustomBiomesManager {
    /**
     * A set of all registered biomes.
     */
    private static final IntegrationRegistry<CustomBiomesIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new biomes integration.
     *
     * @param biomesIntegration The biomes integration to register.
     */
    public static void register(@NotNull final CustomBiomesIntegration biomesIntegration) {
        if (biomesIntegration instanceof Listener) {
            Eco.get().getEcoPlugin().getEventManager().registerListener((Listener) biomesIntegration);
        }
        REGISTRY.register(biomesIntegration);
    }

    @Nullable
    public static CustomBiome getBiomeAt(@NotNull Location location) {
        World world = location.getWorld();

        if (world == null) {
            return null;
        }

        Biome vanilla = world.getBiome(location);

        if (vanilla.name().equalsIgnoreCase("custom")) {
            for (CustomBiomesIntegration integration : REGISTRY) {
                CustomBiome biome = integration.getBiome(location);

                if (biome != null) {
                    return biome;
                }
            }

            return null;
        } else {
            return new CustomBiome(vanilla.name());
        }
    }

    private CustomBiomesManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
