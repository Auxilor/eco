package com.willfp.eco.core.config.interfaces;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
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
     * This used to represent the underlying config, but since 6.30.0 configs use
     * their own implementations internally, without relying on bukkit.
     *
     * @return The config, or null if config is not yaml-based.
     * @deprecated Use toBukkit() instead.
     */
    @Nullable
    @Deprecated(since = "6.30.0", forRemoval = true)
    default YamlConfiguration getBukkitHandle() {
        return this.toBukkit();
    }

    /**
     * Convert the config to a bukkit {@link YamlConfiguration}.
     */
    @NotNull
    YamlConfiguration toBukkit();
}
