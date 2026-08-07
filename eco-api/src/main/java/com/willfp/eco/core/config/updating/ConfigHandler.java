package com.willfp.eco.core.config.updating;

import com.willfp.eco.core.config.interfaces.LoadableConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Every {@link com.willfp.eco.core.PluginLike} has a config handler.
 * <p>
 * Handles updating and saving configs.
 */
public interface ConfigHandler {
    /**
     * Invoke all update methods.
     *
     * @deprecated Part of the reflective reload system that has been removed;
     * this does nothing. Use {@link #updateConfigs()} instead.
     */
    @Deprecated(since = "6.77.2", forRemoval = true)
    default void callUpdate() {
        // Do nothing
    }

    /**
     * Save all registered configs to disk.
     */
    void saveAllConfigs();

    /**
     * Update all registered configs.
     * <p>
     * Updatable configs are updated against their defaults; other loadable configs
     * are simply reloaded from file.
     */
    void updateConfigs();

    /**
     * Register a config to be saved and updated by this handler.
     *
     * @param config The config.
     */
    void addConfig(@NotNull LoadableConfig config);
}
