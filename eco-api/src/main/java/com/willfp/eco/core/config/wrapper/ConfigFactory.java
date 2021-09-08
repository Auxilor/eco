package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Internal component to create backend config implementations.
 */
public interface ConfigFactory {
    /**
     * Updatable config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     * @return The config implementation.
     */
    Config createUpdatableYamlConfig(@NotNull String configName,
                                     @NotNull EcoPlugin plugin,
                                     @NotNull String subDirectoryPath,
                                     @NotNull Class<?> source,
                                     boolean removeUnused,
                                     @NotNull String... updateBlacklist);

    /**
     * Updatable config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     * @param removeUnused     Whether keys not present in the default config should be removed on update.
     * @param updateBlacklist  Substring of keys to not add/remove keys for.
     * @return The config implementation.
     */
    JSONConfig createUpdatableJSONConfig(@NotNull String configName,
                                         @NotNull EcoPlugin plugin,
                                         @NotNull String subDirectoryPath,
                                         @NotNull Class<?> source,
                                         boolean removeUnused,
                                         @NotNull String... updateBlacklist);

    /**
     * JSON loadable config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     * @return The config implementation.
     */
    JSONConfig createLoadableJSONConfig(@NotNull String configName,
                                        @NotNull EcoPlugin plugin,
                                        @NotNull String subDirectoryPath,
                                        @NotNull Class<?> source);

    /**
     * Yaml loadable config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     * @return The config implementation.
     */
    Config createLoadableYamlConfig(@NotNull String configName,
                                    @NotNull EcoPlugin plugin,
                                    @NotNull String subDirectoryPath,
                                    @NotNull Class<?> source);

    /**
     * Yaml config.
     *
     * @param config The handle.
     * @return The config implementation.
     */
    Config createYamlConfig(@NotNull YamlConfiguration config);

    /**
     * JSON config.
     *
     * @param values The values.
     * @return The config implementation.
     */
    JSONConfig createJSONConfig(@NotNull Map<String, Object> values);
}
