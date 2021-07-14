package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.Config;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfigWrapper<T extends Config> extends ConfigWrapper<T> implements WrappedBukkitConfig<YamlConfiguration> {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public YamlConfigWrapper(@NotNull final T handle) {
        super(handle);
    }
}
