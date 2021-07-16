package com.willfp.eco.core.config.updating;

import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.jetbrains.annotations.NotNull;

public interface ConfigHandler {
    /**
     * Invoke all update methods.
     */
    void callUpdate();

    /**
     * Save all configs.
     */
    void saveAllConfigs();

    /**
     * Update all updatable configs.
     */
    void updateConfigs();

    /**
     * Add new config to be saved.
     *
     * @param config The config.
     */
    void addConfig(@NotNull LoadableConfig config);
}
