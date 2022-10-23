package com.willfp.eco.core.config;

import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.wrapper.ConfigWrapper;
import org.jetbrains.annotations.NotNull;

/**
 * Generic config to simplify creating custom configs without having
 * to meddle with delegation.
 */
public abstract class GenericConfig extends ConfigWrapper<Config> {
    /**
     * Create a new generic config.
     */
    protected GenericConfig() {
        super(Configs.empty());
    }

    /**
     * Create a new generic config.
     *
     * @param type The config type.
     */
    protected GenericConfig(@NotNull final ConfigType type) {
        super(Configs.empty(type));
    }
}
