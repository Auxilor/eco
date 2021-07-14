package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.json.JSONConfig;
import com.willfp.eco.core.config.json.LoadableJSONConfig;
import com.willfp.eco.core.config.yaml.LoadableYamlConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

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
     */
    LoadableYamlConfig createUpdatableYamlConfig(@NotNull String configName,
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
     */
    LoadableJSONConfig createLoadableJSONConfig(@NotNull String configName,
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
     */
    LoadableYamlConfig createLoadableYamlConfig(@NotNull String configName,
                                                @NotNull EcoPlugin plugin,
                                                @NotNull String subDirectoryPath,
                                                @NotNull Class<?> source);

    /**
     * Yaml config.
     *
     * @param config The handle.
     */
    Config createYamlConfig(@NotNull YamlConfiguration config);

    /**
     * JSON config.
     *
     * @param values The values.
     */
    JSONConfig createJSONConfig(@NotNull Map<String, Object> values);
}
