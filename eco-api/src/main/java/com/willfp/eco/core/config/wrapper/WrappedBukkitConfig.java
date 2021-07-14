package com.willfp.eco.core.config.wrapper;

import org.bukkit.configuration.ConfigurationSection;

public interface WrappedBukkitConfig<T extends ConfigurationSection> {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    T getBukkitHandle();
}
