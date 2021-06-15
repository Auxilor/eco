package com.willfp.eco.core.config;

import com.willfp.eco.internal.config.LoadableConfig;
import org.jetbrains.annotations.NotNull;

public interface ConfigSaveHandler {
    /**
     * Save all configs for the plugin.
     */
    void saveAllConfigs();

    /**
     * Add a config to be saved.
     *
     * @param config The config.
     */
    void addConfig(@NotNull LoadableConfig config);
}
