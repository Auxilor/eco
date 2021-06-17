package com.willfp.eco.core.config;

import com.willfp.eco.internal.config.yaml.YamlConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfig extends YamlConfigWrapper<YamlConfiguration> {
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
