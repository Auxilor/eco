package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.wrapper.YamlConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;

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

    /**
     * Config implementation for passing YamlConfigurations.
     * <p>
     * Does not automatically update.
     *
     * @param contents     The contents of the config.
     */
    public YamlConfig(@NotNull final String contents) {
        super(Eco.getHandler().getConfigFactory().createYamlConfig(YamlConfiguration.loadConfiguration(new StringReader(contents))));
    }
}
