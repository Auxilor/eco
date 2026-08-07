package com.willfp.eco.core.config.interfaces;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for configs that physically exist as files in plugins.
 */
public interface LoadableConfig extends Config {
    /**
     * Create the file on disk if it does not already exist.
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
     * Save the config asynchronously.
     * <p>
     * The default implementation spawns a raw thread and swallows any {@link IOException}
     * by printing its stack trace; it exists purely for backwards compatibility with
     * legacy {@link Config} implementations. eco's own implementations override this.
     */
    default void saveAsync() {
        // This default implementation exists purely for backwards compatibility
        // with legacy Config implementations that don't have saveAsync().
        // Default eco implementations of Config have saveAsync() implemented.
        new Thread(() -> {
            try {
                this.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

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
     * Convert the config to a bukkit {@link YamlConfiguration}.
     *
     * @return The bukkit config.
     */
    @NotNull
    YamlConfiguration toBukkit();
}
