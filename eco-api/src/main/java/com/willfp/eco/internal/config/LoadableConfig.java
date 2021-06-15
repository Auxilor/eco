package com.willfp.eco.internal.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public interface LoadableConfig {
    /**
     * Create the file.
     */
    void createFile();

    /**
     * Get resource path as relative to base directory.
     *
     * @return The resource path.
     */
    String getResourcePath();

    /**
     * Get YamlConfiguration as found in jar.
     *
     * @return The YamlConfiguration.
     */
    YamlConfiguration getConfigInJar();

    /**
     * Save the config.
     *
     * @throws IOException If error in saving.
     */
    void save() throws IOException;

    /**
     * Get the config file.
     *
     * @return The file.
     */
    File getConfigFile();

    /**
     * Get the config name (including extension)
     *
     * @return The name.
     */
    String getName();
}
