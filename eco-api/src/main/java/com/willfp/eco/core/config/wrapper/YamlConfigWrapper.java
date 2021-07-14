package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfigWrapper extends ConfigWrapper<Config> implements WrappedBukkitConfig<YamlConfiguration> {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public YamlConfigWrapper(@NotNull final Config handle) {
        super(handle);
    }
}
