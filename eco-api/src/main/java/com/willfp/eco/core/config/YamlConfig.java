package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.wrapper.YamlConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfig extends YamlConfigWrapper {
    /**
     * Config implementation for passing YamlConfigurations.
     * <p>
     * Does not automatically update.
     *
     * @param config     The YamlConfiguration handle.
     */
    public YamlConfig(@NotNull final YamlConfiguration config) {
        super(Eco.getHandler().getConfigFactory().createYamlConfig(config));
    }
}
