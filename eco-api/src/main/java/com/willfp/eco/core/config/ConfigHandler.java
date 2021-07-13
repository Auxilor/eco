package com.willfp.eco.core.config;

import org.jetbrains.annotations.NotNull;

public interface ConfigHandler {
    /**
     * Invoke all update methods.
     */
    void callUpdate();

    /**
     * Register an updatable class.
     *
     * @param updatableClass The class with an update method.
     */
    void registerUpdatableClass(@NotNull Class<?> updatableClass);

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
