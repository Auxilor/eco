package com.willfp.eco.core.blocks.provider;

import com.willfp.eco.core.blocks.TestableBlock;
import com.willfp.eco.core.registry.Registry;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Block providers are call-site registrations for blocks. In other words,
 * they only register their blocks when a request is made. This is marginally
 * slower, however it is required for certain plugins, and fixes bugs related to
 * loading orders.
 *
 * @see TestableBlock
 */
public abstract class BlockProvider {
    /**
     * The namespace.
     */
    private final String namespace;

    /**
     * Create a new BlockProvider for a specific namespace.
     *
     * @param namespace The namespace.
     */
    protected BlockProvider(@NotNull final String namespace) {
        this.namespace = namespace;
    }

    /**
     * Provide a TestableBlock for a given key.
     *
     * @param key The block ID.
     * @return The TestableBlock, or null if not found.
     */
    @Nullable
    public abstract TestableBlock provideForKey(@NotNull String key);

    /**
     * Get the namespace.
     *
     * @return The namespace.
     */
    public String getNamespace() {
        return this.namespace;
    }
}
