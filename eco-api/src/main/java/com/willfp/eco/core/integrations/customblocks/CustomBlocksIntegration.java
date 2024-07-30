package com.willfp.eco.core.integrations.customblocks;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper class for custom block integrations.
 */
public interface CustomBlocksIntegration extends Integration {
    /**
     * Register all the custom block for a specific plugin into eco.
     *
     * @see com.willfp.eco.core.blocks.Blocks
     */
    default void registerAllBlocks() {
        // Override when needed.
    }

    /**
     * Register {@link com.willfp.eco.core.blocks.provider.BlockProvider}s.
     */
    default void registerProvider() {
        // Override when needed.
    }
}
