package com.willfp.eco.core.integrations.customblocks;

import com.willfp.eco.core.integrations.IntegrationRegistry;
import org.jetbrains.annotations.NotNull;

/**
 * Class to handle custom block integrations.
 */
public final class CustomBlocksManager {
    /**
     * A set of all registered integrations.
     */
    private static final IntegrationRegistry<CustomBlocksIntegration> REGISTRY = new IntegrationRegistry<>();

    /**
     * Register a new integration.
     *
     * @param integration The integration to register.
     */
    public static void register(@NotNull final CustomBlocksIntegration integration) {
        REGISTRY.register(integration);
    }

    /**
     * Register all the custom block for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.blocks.Blocks
     */
    public static void registerAllBlocks() {
        REGISTRY.forEachSafely(CustomBlocksIntegration::registerAllBlocks);
    }

    /**
     * Register all the custom blocks for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.blocks.Blocks
     */
    public static void registerProviders() {
        REGISTRY.forEachSafely(CustomBlocksIntegration::registerProvider);
    }

    private CustomBlocksManager() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
