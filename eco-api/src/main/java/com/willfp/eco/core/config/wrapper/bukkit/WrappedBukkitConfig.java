package com.willfp.eco.core.config.wrapper.bukkit;

import org.bukkit.configuration.ConfigurationSection;

public interface WrappedBukkitConfig<T extends ConfigurationSection> {
    /**
     * Get the ConfigurationSection handle.
     *
     * @return The handle.
     */
    T getBukkitHandle();
}
