package com.willfp.eco.core.integrations.customblocks;

import com.willfp.eco.core.integrations.Integration;

/**
 * Wrapper interface for custom block integrations.
 * <p>
 * Implemented for plugins that add custom blocks, such as ItemsAdder, Oraxen, Nexo, and
 * CraftEngine, so that their blocks can be looked up through eco's own block system.
 *
 * @see CustomBlocksManager
 */
public interface CustomBlocksIntegration extends Integration {
    /**
     * Register all of this plugin's custom blocks into eco.
     *
     * @see com.willfp.eco.core.blocks.Blocks
     */
    default void registerAllBlocks() {
        // Override when needed.
    }

    /**
     * Register this plugin's {@link com.willfp.eco.core.blocks.provider.BlockProvider}s into eco.
     */
    default void registerProvider() {
        // Override when needed.
    }
}
