package com.willfp.eco.core.config;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Config implementation for passing YamlConfigurations.
 * <p>
 * Does not automatically update.
 */
public class TransientConfig extends ConfigWrapper<Config> {
    /**
     * @param config The YamlConfiguration handle.
     */
    public TransientConfig(@NotNull final YamlConfiguration config) {
        super(Eco.getHandler().getConfigFactory().createConfig(config));
    }

    /**
     * Create a new empty transient config.
     *
     * @param values The values.
     */
    public TransientConfig(@NotNull final Map<String, Object> values) {
        super(Eco.getHandler().getConfigFactory().createConfig(values));
    }

    /**
     * Create a new empty transient config.
     */
    public TransientConfig() {
        super(Eco.getHandler().getConfigFactory().createConfig("", ConfigType.YAML));
    }

    /**
     * @param contents The contents of the config.
     * @param type     The config type.
     */
    public TransientConfig(@NotNull final String contents,
                           @NotNull final ConfigType type) {
        super(Eco.getHandler().getConfigFactory().createConfig(contents, type));
    }
}
