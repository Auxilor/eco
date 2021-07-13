package com.willfp.eco.core.config.wrapper;

import com.willfp.eco.core.config.Config;
import org.jetbrains.annotations.NotNull;

public abstract class YamlConfigWrapper extends ConfigWrapper<Config> {
    /**
     * Create a config wrapper.
     *
     * @param handle The handle.
     */
    public YamlConfigWrapper(@NotNull final Config handle) {
        super(handle);
    }
}
