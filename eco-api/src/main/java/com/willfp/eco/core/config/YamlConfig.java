package com.willfp.eco.core.config;

import com.willfp.eco.internal.config.yaml.ConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfig extends ConfigWrapper<YamlConfiguration> {
    /**
     * Config implementation for passing YamlConfigurations.
     * <p>
     * Does not automatically update.
     *
     * @param config     The YamlConfiguration handle.
     */
    public YamlConfig(@NotNull final YamlConfiguration config) {
        init(config);
    }
}
