package com.willfp.eco.util.config;

import com.willfp.eco.internal.config.AbstractUndefinedConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class StaticOptionalConfig extends AbstractUndefinedConfig {
    /**
     * Config implementation for passing YamlConfigurations.
     * <p>
     * Does not automatically update.
     *
     * @param configName The name of the config
     * @param plugin     The plugin.
     * @param config     The YamlConfiguration handle.
     */
    public StaticOptionalConfig(@NotNull final String configName,
                                @NotNull final AbstractEcoPlugin plugin,
                                @NotNull final YamlConfiguration config) {
        super(configName, plugin);

        init(config);
    }
}
