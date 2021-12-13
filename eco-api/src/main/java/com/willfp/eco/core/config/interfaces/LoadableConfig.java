package com.willfp.eco.core.config.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Interface for configs that physically exist as files in plugins.
 */
public interface LoadableConfig extends Config {
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
     * Get the config name (including extension).
     *
     * @return The name.
     */
    String getName();

    /**
     * Get bukkit {@link YamlConfiguration}.
     * <p>
     * This method is not recommended unless absolutely required as it
     * only returns true if {@link this#getType()} is {@link com.willfp.eco.core.config.ConfigType#YAML},
     * and if the handle is an {@link YamlConfiguration} specifically. This depends on the internals
     * and the implementation, and so may cause problems - it exists mostly for parity with
     * {@link JavaPlugin#getConfig()}.
     *
     * @return The config, or null if config is not yaml-based.
     */
    @Nullable
    YamlConfiguration getBukkitHandle();
}
