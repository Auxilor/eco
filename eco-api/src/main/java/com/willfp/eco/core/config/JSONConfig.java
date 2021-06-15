package com.willfp.eco.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface JSONConfig extends Config {
    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or a blank {@link java.util.ArrayList} if not found.
     */
    @NotNull
    List<JSONConfig> getSubsections(@NotNull String path);

    /**
     * Get a list of subsections from config.
     *
     * @param path The key to fetch the value from.
     * @return The found value, or null if not found.
     */
    @Nullable
    List<JSONConfig> getSubsectionsOrNull(@NotNull String path);
}
