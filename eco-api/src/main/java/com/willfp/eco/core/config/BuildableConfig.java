package com.willfp.eco.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder for configs to create them programmatically.
 */
public class BuildableConfig extends TransientConfig {
    /**
     * Create a new empty config builder.
     */
    public BuildableConfig() {
        super();
    }

    /**
     * Add to the config builder.
     *
     * @param path   The path.
     * @param object The object.
     * @return The builder.
     */
    public BuildableConfig add(@NotNull final String path,
                               @Nullable final Object object) {
        set(path, object);
        return this;
    }
}
