package com.willfp.eco.core.config.yaml;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.yaml.wrapper.YamlConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;

/**
 * Config implementation for passing YamlConfigurations.
 * <p>
 * Does not automatically update.
 */
public class YamlTransientConfig extends YamlConfigWrapper {
    /**
     * @param config The YamlConfiguration handle.
     */
    public YamlTransientConfig(@NotNull final YamlConfiguration config) {
        super(Eco.getHandler().getConfigFactory().createYamlConfig(config));
    }

    /**
     * @param contents The contents of the config.
     */
    public YamlTransientConfig(@NotNull final String contents) {
        super(Eco.getHandler().getConfigFactory().createYamlConfig(YamlConfiguration.loadConfiguration(new StringReader(contents))));
    }

    /**
     * Create a new empty transient config.
     */
    public YamlTransientConfig() {
        super(Eco.getHandler().getConfigFactory().createYamlConfig(YamlConfiguration.loadConfiguration(new StringReader(""))));
    }
}
