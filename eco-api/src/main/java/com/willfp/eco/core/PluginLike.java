package com.willfp.eco.core;

import com.willfp.eco.core.config.updating.ConfigHandler;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents any class that acts like a plugin, for example {@link EcoPlugin}
 * or {@link com.willfp.eco.core.extensions.Extension}. This exists to create
 * things such as extension base configs rather than needing to pass an instance
 * of the owning plugin.
 */
public interface PluginLike {
    /**
     * Get the data folder of the object.
     * <p>
     * Returns the plugin data folder for a plugin, or the extension's parent plugin's folder
     *
     * @return The data folder.
     */
    File getDataFolder();

    /**
     * Get the handler class for updatable classes.
     *
     * @return The config handler.
     */
    ConfigHandler getConfigHandler();

    /**
     * Get the logger.
     *
     * @return The logger.
     */
    Logger getLogger();
}
