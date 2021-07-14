package com.willfp.eco.internal.config;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.wrapper.ConfigFactory;
import com.willfp.eco.internal.config.json.EcoJSONConfigSection;
import com.willfp.eco.internal.config.json.EcoLoadableJSONConfig;
import com.willfp.eco.internal.config.yaml.EcoLoadableYamlConfig;
import com.willfp.eco.internal.config.yaml.EcoUpdatableYamlConfig;
import com.willfp.eco.internal.config.yaml.EcoYamlConfigSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EcoConfigFactory implements ConfigFactory {
    @Override
    public Config createUpdatableYamlConfig(@NotNull final String configName,
                                            @NotNull final EcoPlugin plugin,
                                            @NotNull final String subDirectoryPath,
                                            @NotNull final Class<?> source,
                                            final boolean removeUnused,
                                            @NotNull final String... updateBlacklist) {
        return new EcoUpdatableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source,
                removeUnused,
                updateBlacklist
        );
    }

    @Override
    public JSONConfig createLoadableJSONConfig(@NotNull final String configName,
                                               @NotNull final EcoPlugin plugin,
                                               @NotNull final String subDirectoryPath,
                                               @NotNull final Class<?> source) {
        return new EcoLoadableJSONConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
        );
    }

    @Override
    public Config createLoadableYamlConfig(@NotNull final String configName,
                                           @NotNull final EcoPlugin plugin,
                                           @NotNull final String subDirectoryPath,
                                           @NotNull final Class<?> source) {
        return new EcoLoadableYamlConfig(
                configName,
                plugin,
                subDirectoryPath,
                source
        );
    }

    @Override
    public Config createYamlConfig(@NotNull final YamlConfiguration config) {
        return new EcoYamlConfigSection(config);
    }

    @Override
    public JSONConfig createJSONConfig(@NotNull final Map<String, Object> values) {
        return new EcoJSONConfigSection(values);
    }
}
