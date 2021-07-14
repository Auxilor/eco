package com.willfp.eco.internal.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.Config;
import com.willfp.eco.core.config.json.JSONConfig;
import com.willfp.eco.core.config.wrapper.ConfigFactory;
import com.willfp.eco.internal.config.json.JSONConfigSection;
import com.willfp.eco.internal.config.json.LoadableJSONConfig;
import com.willfp.eco.internal.config.yaml.LoadableYamlConfig;
import com.willfp.eco.internal.config.yaml.UpdatableYamlConfig;
import com.willfp.eco.internal.config.yaml.YamlConfigSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EcoConfigFactory implements ConfigFactory {
    @Override
    public com.willfp.eco.core.config.yaml.LoadableYamlConfig createUpdatableYamlConfig(@NotNull final String configName,
                                                                                        @NotNull final EcoPlugin plugin,
                                                                                        @NotNull final String subDirectoryPath,
                                                                                        @NotNull final Class<?> source,
                                                                                        final boolean removeUnused,
                                                                                        @NotNull final String... updateBlacklist) {
        return new UpdatableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source,
                removeUnused,
                updateBlacklist
        );
    }

    @Override
    public com.willfp.eco.core.config.json.LoadableJSONConfig createLoadableJSONConfig(@NotNull final String configName,
                                                                                       @NotNull final EcoPlugin plugin,
                                                                                       @NotNull final String subDirectoryPath,
                                                                                       @NotNull final Class<?> source) {
        return new LoadableJSONConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
        );
    }

    @Override
    public com.willfp.eco.core.config.yaml.LoadableYamlConfig createLoadableYamlConfig(@NotNull final String configName,
                                                                                       @NotNull final EcoPlugin plugin,
                                                                                       @NotNull final String subDirectoryPath,
                                                                                       @NotNull final Class<?> source) {
        return new LoadableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
        );
    }

    @Override
    public Config createYamlConfig(@NotNull final YamlConfiguration config) {
        return new YamlConfigSection(config);
    }

    @Override
    public JSONConfig createJSONConfig(@NotNull final Map<String, Object> values) {
        return new JSONConfigSection(values);
    }
}
